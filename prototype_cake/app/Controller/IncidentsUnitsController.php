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
 * IncidentsUnits Controller
 *
 * @property IncidentsUnit $IncidentsUnit
 */
class IncidentsUnitsController extends AppController {

  /**
   * add method
   *
   * @throws BadRequestException
   * @return void
   */
  public function add() {
    $this->_add(array(
      'success' => __('The association has been saved'),
      'redirect' =>
      (isset($this->request->params['named']['return']) && ($this->request->params['named']['return'] == 'unit')) ?
              array('controller' => 'units', 'action' => 'view', 'id' => 'unit_id') :
              array('controller' => 'incidents', 'action' => 'view', 'id' => 'incident_id'),
      'failure' => __('The association could not be saved. Please try again.'),
    ));

    if (!$this->request->is('post')) {
      if (isset($this->request->params['named']['incident_id'])) {
        $this->request->data['IncidentsUnit']['incident_id'] = $this->request->params['named']['incident_id'];
      }
      if (isset($this->request->params['named']['unit_id'])) {
        $this->request->data['IncidentsUnit']['unit_id'] = $this->request->params['named']['unit_id'];
      }
    }
    $incidents = $this->IncidentsUnit->Incident->find('list');
    $units = $this->IncidentsUnit->Unit->find('list');
    $this->set(compact('incidents', 'units'));
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
      'notfound' => __('Invalid association'),
      'success' => __('The association has been saved'),
      'redirect' =>
      (isset($this->request->params['named']['return']) && ($this->request->params['named']['return'] == 'unit')) ?
              array('controller' => 'units', 'action' => 'view', 'id' => 'unit_id') :
              array('controller' => 'incidents', 'action' => 'view', 'id' => 'incident_id'),
      'failure' => __('The association could not be saved. Please try again.'),
    ));
  }

}
