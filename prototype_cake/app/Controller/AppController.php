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
App::uses('Controller', 'Controller');

/**
 * Application Controller
 *
 * Add your application-wide methods in the class below, your controllers
 * will inherit them.
 *
 * @link          http://book.cakephp.org/2.0/en/controllers.html#the-app-controller
 */
class AppController extends Controller {

  /**
   * Additional components to load
   *
   * @var array
   */
  public $components = array(
    'RequestHandler',
  );

  /**
   * Set objects to serialize
   *
   * @param array $data The key value pairs to serialize
   * @return boolean false if no JSON or XML view was requested
   */
  protected function _serialize($data) {
    if (!in_array($this->RequestHandler->ext, array('json', 'xml'))) {
      return false;
    }

    $this->set($data);
    $this->set('_serialize', array_keys($data));
    return true;
  }

  /**
   * Generic method to add an entry
   *
   * @throws BadRequestException
   * @param array $options Additional options
   */
  protected function _add($options = array()) {
    //Select the model
    $model = &$this->{$this->modelClass};

    $model->setRequired();
    if ($this->request->is('post')) {
      //Throws an exception if id is set in data
      $model->setCleanId($this->request->data, false);

      $model->create();
      $this->_save($options);
    }
  }

  /**
   * Generic method to edit an entry
   *
   * @throws BadRequestException
   * @throws NotFoundException
   * @param string $id The entry id
   * @param array $options Additional options
   */
  protected function _edit($id = null, $options = array()) {
    //Select the model
    $model = &$this->{$this->modelClass};

    $id = $model->setCleanId($this->request->data, $id);
    if (!$model->exists()) {
      throw new NotFoundException(isset($options['notfound']) ? $options['notfound'] : __('Invalid id'));
    }
    if ($this->request->is('post') || $this->request->is('put')) {
      $this->_save($options);
    } else {
      $this->request->data = $model->getRecord();
    }
  }

  /**
   * Generic method to save an entry
   *
   * @param array $options Additional options
   */
  protected function _save($options = array()) {
    //Select the model
    $model = &$this->{$this->modelClass};

    if ($model->save($this->request->data)) {
      if (!$this->_serialize(array('success' => true, 'id' => $model->id))) {
        $this->Session->setFlash(isset($options['success']) ? $options['success'] : __('The data has been saved'));
        if (isset($options['redirect'])) {
          if (isset($options['redirect']['id'])) {
            $options['redirect'][] = $model->field($options['redirect']['id']);
            unset($options['redirect']['id']);
          }
          $this->redirect($options['redirect']);
        } else {
          $this->redirect(array('action' => 'view', $model->id));
        }
      }
    } else {
      if (!$this->_serialize(array('success' => false, 'errors' => $model->validationErrors))) {
        $this->Session->setFlash(isset($options['failure']) ? $options['failure'] : __('The data could not be saved. Please, try again.'));
      }
    }
  }

}
