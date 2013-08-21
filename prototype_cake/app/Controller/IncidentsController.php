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
App::uses('AppController', 'Controller');

/**
 * Incidents Controller
 *
 * @property Incident $Incident
 */
class IncidentsController extends AppController {

  /**
   * index method
   *
   * @return void
   */
  public function index() {
    $this->set('incidents', $this->paginate());
    $this->set('_serialize', 'incidents');
  }

  /**
   * view method
   *
   * @throws NotFoundException
   * @param string $id
   * @return void
   */
  public function view($id = null) {
    $this->Incident->recursive = 1;
    if (!$this->Incident->exists($id)) {
      throw new NotFoundException(__('Invalid incident'));
    }
    $this->set('incident', $this->Incident->getRecord($id));
    $this->set('_serialize', 'incident');
  }

  /**
   * add method
   *
   * @throws BadRequestException
   * @return void
   */
  public function add() {
    $this->_add(array(
      'success' => __('The incident has been saved'),
      'failure' => __('The incident could not be saved. Please try again.'),
    ));
  }

  /**
   * edit method
   *
   * @throws BadRequestException
   * @throws NotFoundException
   * @param string $id
   * @return void
   */
  public function edit($id = null) {
    $this->_edit($id, array(
      'notfound' => __('Invalid incident'),
      'success' => __('The incident has been saved'),
      'failure' => __('The incident could not be saved. Please try again.'),
    ));
  }

}
