<?php

/**
 * WRK CoCeSo
 * Copyright (c) Daniel Rohr
 *
 * Licensed under The MIT License
 * For full copyright and license information, please see the LICENSE.txt
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright (c) 2013 Daniel Rohr
 * @link          https://sourceforge.net/projects/coceso/
 * @package       coceso.prototype
 * @since         Rev. 1
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
App::uses('AppModel', 'Model');

/**
 * IncidentsUnit Model
 *
 * @property Incident $Incident
 * @property Unit $Unit
 */
class IncidentsUnit extends AppModel {

  /**
   * Whitelist of fields allowed to be saved.
   *
   * @var array
   */
  public $whitelist = array('incident_id', 'unit_id', 'status');

  /**
   * Validation rules
   *
   * @var array
   */
  public $validate = array(
    'incident_id' => array(
      'hasAnyForeign' => array(
        'rule' => array('hasAnyForeign', 'Incident', array('incident_id' => 'id')),
      ),
      'isUniquePair' => array(
        'rule' => array('isUniquePair', 'unit_id')
      ),
    ),
    'unit_id' => array(
      'hasAnyForeign' => array(
        'rule' => array('hasAnyForeign', 'Unit', array('unit_id' => 'id')),
      ),
    ),
    'status' => array(
      'inlist' => array(
        'rule' => array('inlist', array('0', '1', '2', '3', '4', '5')),
        'required' => false,
      ),
    ),
  );

  /**
   * belongsTo associations
   *
   * @var array
   */
  public $belongsTo = array(
    'Incident' => array(
      'className' => 'Incident',
      'foreignKey' => 'incident_id',
      'conditions' => '',
      'fields' => '',
      'order' => ''
    ),
    'Unit' => array(
      'className' => 'Unit',
      'foreignKey' => 'unit_id',
      'conditions' => '',
      'fields' => '',
      'order' => ''
    )
  );

  /**
   * Called before each save operation, after validation. Return a non-true result
   * to halt the save.
   *
   * @param array $options
   * @return boolean True if the operation should continue, false if it should abort
   * @link http://book.cakephp.org/2.0/en/models/callback-methods.html#beforesave
   * @see Model::beforeSave()
   */
  public function beforeSave($options = array()) {
    return true;
  }

  /**
   * Called after each successful save operation.
   *
   * @param boolean $created True if this save created a new record
   * @return void
   * @link http://book.cakephp.org/2.0/en/models/callback-methods.html#aftersave
   * @see Model::afterSave
   */
  public function afterSave($created) {
    $data = $this->getRecord(null, array('recursive' => 0));
    if (empty($data)) {
      return;
    }

    $status = $data['IncidentsUnit']['status'];
    $incidentStatus = null;

    switch ($data['Incident']['status']) {
      case 0:
      case 1:
        //New or open incident: no changes
        break;
      case 2:
        if (($status == 0) && !$this->find('count', array('incident_id' => $data['IncidentsUnit']['incident_id'], 'status' => array(1, 2)))) {
          //Unit was removed and no other unit remains: set to open
          //TODO: What if there are units in status 3,4,5?
          $incidentStatus = 1;
        } else if (($status == 3) || ($status == 4) || ($status == 5)) {
          //In progress
          $incidentStatus = 3;
        }
        break;
      case 3:
        if (($status <= 2) && !$this->find('count', array('incident_id' => $data['IncidentsUnit']['incident_id'], 'status' => array(3, 4, 5)))) {
          //Status changed to something not in progress and no other units in progress are assigned
          if ($status == 0) {
            if ($this->find('count', array('incident_id' => $data['IncidentsUnit']['incident_id'], 'status' => array(1, 2)))) {
              //Unit was removed, other units in status 1 or 2: set to scheduled
              $incidentStatus = 2;
            } else {
              //No other assigned units: set to finished
              $incidentStatus = 4;
            }
          } else {
            //Unit is assigned or on the way: set to scheduled
            $incidentStatus = 2;
          }
        }
        break;
      case 4:
        if (($status == 1) || ($status == 2)) {
          //Scheduled
          $incidentStatus = 2;
        } else if (($status == 3) || ($status == 4) || ($status == 5)) {
          //In progress
          $incidentStatus = 3;
        }
        break;
    }

    if (!is_null($incidentStatus)) {
      $this->Incident->set(array('id' => $data['IncidentsUnit']['incident_id'], 'status' => $incidentStatus));
      $this->Incident->save(null, false);
    }

    if (($data['Incident']['type'] == 3) && ($data['IncidentsUnit']['status'] > 0) && ($data['Unit']['status'] > 1)) {
      $this->Unit->set(array('id' => $data['IncidentsUnit']['unit_id'], 'status' => 1));
      $this->Unit->save(null, false);
    }
  }

  /**
   * Override delete method to disable it
   *
   * @return boolean false
   * @see Model::delete()
   */
  public function delete($id = null, $cascade = true) {
    return false;
  }

}
