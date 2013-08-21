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
 * Unit Model
 *
 * @property Incident $Incident
 * @property IncidentsUnit $IncidentsUnit
 */
class Unit extends AppModel {

  /**
   * Display field
   *
   * @var string
   */
  public $displayField = 'short';

  /**
   * Whitelist of fields allowed to be saved.
   *
   * @var array
   */
  public $whitelist = array('name', 'short', 'type', 'status');

  /**
   * Validation rules
   *
   * @var array
   */
  public $validate = array(
    'name' => array(
      'notempty' => array(
        'rule' => 'notempty',
      ),
      'isUnique' => array(
        'rule' => 'isUnique',
      ),
    ),
    'short' => array(
      'notempty' => array(
        'rule' => 'notempty',
      ),
      'isUnique' => array(
        'rule' => 'isUnique',
      ),
    ),
    'type' => array(
      'inlist' => array(
        'rule' => array('inlist', array('Trupp', 'KFZ')),
      ),
    ),
    'status' => array(
      'inlist' => array(
        'rule' => array('inlist', array('0', '1', '2', '3')),
        'required' => false,
      ),
    ),
  );

  /**
   * hasAndBelongsToMany associations
   *
   * @var array
   */
  public $hasAndBelongsToMany = array(
    'Incident' => array(
      'className' => 'Incident',
      'joinTable' => 'incidents_units',
      'with' => 'IncidentsUnit',
      'foreignKey' => 'unit_id',
      'associationForeignKey' => 'incident_id',
      'unique' => 'keepExisting',
      'conditions' => '',
      'fields' => '',
      'order' => 'Incident.id DESC',
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
    //Creating unit-incident associations is not allowed through this model
    unset($this->data['Incident']);

    return true;
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
