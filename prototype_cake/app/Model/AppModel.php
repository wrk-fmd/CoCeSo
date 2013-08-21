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
 * @package       coceso.core
 * @since         Rev. 1
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
App::uses('Model', 'Model');

/**
 * Application model for Cake.
 *
 * Add your application-wide methods in the class below, your models
 * will inherit them.
 *
 */
class AppModel extends Model {

  /**
   * Number of associations to recurse through during find calls.
   * Fetches no associated data by default.
   *
   * @var integer
   * @see Model::$recursive
   * @link http://book.cakephp.org/2.0/en/models/model-attributes.html#recursive
   */
  public $recursive = -1;

  /**
   * Set the required key to validation rules
   *
   * @param boolean $required
   * @return void
   */
  public function setRequired($required = true) {
    foreach ($this->validate as &$field) {
      if (is_array($field) && !isset($field['rule'])) {
        foreach ($field as &$rule) {
          if (is_array($rule) && !isset($rule['required'])) {
            $rule['required'] = $required;
          }
        }
      }
    }
  }

  /**
   * Wrapper for isUnique to check for existing pairs in database
   *
   * @param array $fields Field/value pair to search
   * @param array|string $additional Name or array of names of additional fields
   * @return boolean False if any records matching any fields are found
   */
  public function isUniquePair($fields, $additional) {
    if (is_array($additional)) {
      $fields = array_merge($fields, $additional);
    } else {
      $fields[] = $additional;
    }
    return parent::isUnique($fields, false);
  }

  /**
   * Wrapper for hasAny of associated model
   *
   * @param array $conditions Field/value pairs to search
   * @param string|Model $model Name of model or model itself
   * @param $rewrite Condition columns to rename
   * @return boolean True if a record meeting the conditions exists
   */
  public function hasAnyForeign($conditions, $model, $rewrite = array()) {
    if (is_string($model) && !empty($this->{$model})) {
      $model = &$this->{$model};
    }
    if (!is_a($model, 'Model')) {
      return false;
    }

    foreach ($rewrite as $key => $value) {
      if (isset($conditions[$key])) {
        $conditions[$value] = $conditions[$key];
        unset($conditions[$key]);
      }
    }

    return $model->hasAny($conditions);
  }

  /**
   * Set the id and check for inconsistent id values
   *
   * @param array $data The post data
   * @param string|boolean $id The id or false if id should be unset
   * @return string The id
   * @throws BadRequestException
   */
  public function setCleanId($data, $id) {
    //Format $data, taken from Model::set()
    if (is_object($data)) {
      if ($data instanceof SimpleXMLElement || $data instanceof DOMNode) {
        $data = $this->_normalizeXmlData(Xml::toArray($one));
      } else {
        $data = Set::reverse($data);
      }
    }
    if (empty($data[$this->alias])) {
      $data = $this->_setAliasData($data);
    }

    if (!isset($data[$this->alias][$this->primaryKey])) {
      //No id set in data, hence no problems
      return $this->id = (empty($id) ? null : $id);
    }
    if ($id === false) {
      //No id allowed, but set in POST data
      throw new BadRequestException(__('Inconsistent data'));
    }
    if (empty($id)) {
      //Empty id, use POST data
      return $this->id = (empty($data[$this->alias][$this->primaryKey]) ? null : $data[$this->alias][$this->primaryKey]);
    }
    if ($id !== $data[$this->alias][$this->primaryKey]) {
      //id and POST data don't match
      throw new BadRequestException(__('Inconsistent data'));
    }
    //Values match and are not empty, hence safe to use
    return $this->id = $id;
  }

  /**
   * Get the record with the specified id
   *
   * @param string $id The record to fetch
   * @param array $options Additional options
   * @return array Array of records, or Null on failure.
   */
  public function getRecord($id = null, $options = array()) {
    if (is_null($id)) {
      $id = $this->id;
    }
    $options['conditions'] = array($this->alias . '.' . $this->primaryKey => $id);
    return $this->find('first', $options);
  }

}
