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
 * Units Controller
 *
 * @property Unit $Unit
 */
class UnitsController extends AppController {

  /**
   * index method
   *
   * @return void
   */
  public function index() {
    $this->set('units', $this->paginate());
    $this->set('_serialize', 'units');
  }

  /**
   * view method
   *
   * @throws NotFoundException
   * @param string $id
   * @return void
   */
  public function view($id = null) {
    $this->Unit->recursive = 1;
    if (!$this->Unit->exists($id)) {
      throw new NotFoundException(__('Invalid unit'));
    }
    $this->set('unit', $this->Unit->getRecord($id));
    $this->set('_serialize', 'unit');
  }

  /**
   * add method
   *
   * @throws BadRequestException
   * @return void
   */
  public function add() {
    $this->_add(array(
      'success' => __('The unit has been saved'),
      'failure' => __('The unit could not be saved. Please try again.'),
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
      'notfound' => __('Invalid unit'),
      'success' => __('The unit has been saved'),
      'failure' => __('The unit could not be saved. Please try again.'),
    ));
  }

}
