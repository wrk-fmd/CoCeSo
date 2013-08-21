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
 * Incident Model
 *
 * @property IncidentsUnit $IncidentsUnit
 * @property Unit $Unit
 */
class Incident extends AppModel {

  /**
   * Display field
   *
   * @var string
   */
  public $displayField = 'id';

  /**
   * Whitelist of fields allowed to be saved.
   *
   * @var array
   */
  public $whitelist = array('finished', 'type', 'priority', 'text', 'comment', 'status');

  /**
   * Validation rules
   *
   * @var array
   */
  public $validate = array(
    'type' => array(
      'inlist' => array(
        'rule' => array('inlist', array('1', '2', '3')),
      ),
    ),
    'priority' => array(
      'boolean' => array(
        'rule' => array('boolean'),
        'allowEmpty' => true,
        'required' => false,
      ),
    ),
    'status' => array(
      'inlist' => array(
        'rule' => array('inlist', array('0', '1', '2', '3', '4')),
        'required' => false,
      ),
      'validStatus' => array(
        'rule' => array('validStatus'),
        'required' => false,
      )
    ),
  );

  /**
   * hasAndBelongsToMany associations
   *
   * @var array
   */
  public $hasAndBelongsToMany = array(
    'Unit' => array(
      'className' => 'Unit',
      'joinTable' => 'incidents_units',
      'with' => 'IncidentsUnit',
      'foreignKey' => 'incident_id',
      'associationForeignKey' => 'unit_id',
      'unique' => 'keepExisting',
      'conditions' => '',
      'fields' => '',
      'order' => 'Unit.short ASC',
      'limit' => '',
      'offset' => '',
      'finderQuery' => '',
      'deleteQuery' => '',
      'insertQuery' => ''
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
    //Creating incident-unit associations is not allowed through this model
    unset($this->data['Unit']);

    //Set finished date - taken from Model::save()
    if (isset($this->data['incident']['status'])) {
      if (($this->data['Incident']['status'] == 4) && ($this->field('status') != 4)) {
        $default = array('formatter' => 'date');
        $colType = array_merge($default, $this->getDataSource()->columns[$this->getColumnType('finished')]);
        $this->data['Incident']['finished'] = (!array_key_exists('format', $colType)) ?
                strtotime('now') :
                call_user_func($colType['formatter'], $colType['format']);
      } else if ($this->data['Incident']['status'] != 4) {
        $this->data['Incident']['finished'] = null;
      } else {
        unset($this->data['Incident']['finished']);
      }
    }
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
    if ($this->field('status') == 4) {
      $this->IncidentsUnit->updateAll(array('status' => 0), array('incident_id' => $this->id));
    }
  }

  /**
   * Validate the set status against assigned units
   *
   * @param array $status Array with a key 'status' containing the set status
   * @return boolean
   */
  public function validStatus($status) {
    if (!isset($status['status'])) {
      return false;
    }

    switch ($status['status']) {
      case 1:
      case 4:
        //Open, finished: always possible
        return true;
      case 0:
        //New
        if (!$this->id || !$this->field('status')) {
          return true;
        }
        break;
      case 2:
        //Disp
        if ($this->id && $this->IncidentsUnit->find('count', array('incident_id' => $this->id, 'status' => array(1, 2)))) {
          return true;
        }
        break;
      case 3:
        //In progress
        if ($this->id && $this->IncidentsUnit->find('count', array('incident_id' => $this->id, 'status' => array(3, 4, 5)))) {
          return true;
        }
        break;
    }

    return false;
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
