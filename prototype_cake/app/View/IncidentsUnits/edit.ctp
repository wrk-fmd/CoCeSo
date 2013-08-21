<div class="incidentsUnits form">
  <?php echo $this->Form->create('IncidentsUnit'); ?>
  <fieldset>
    <legend><?php echo __('Edit Incidents Unit'); ?></legend>
    <?php
    echo $this->Form->input('id');
    echo $this->Form->input('status', array('type' => 'select', 'options' => array(0 => 'Nicht zugewiesen', 1 => 'Zugewiesen', 2 => 'ZBO', 3 => 'BO', 4 => 'ZAO', 5 => 'AO')));
    ?>
  </fieldset>
  <?php echo $this->Form->end(__('Submit')); ?>
</div>
<div class="actions">
  <h3><?php echo __('Actions'); ?></h3>
  <ul>
    <li><?php echo $this->Html->link(__('List Incidents Units'), array('action' => 'index')); ?></li>
    <li><?php echo $this->Html->link(__('List Incidents'), array('controller' => 'incidents', 'action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Incident'), array('controller' => 'incidents', 'action' => 'add')); ?> </li>
    <li><?php echo $this->Html->link(__('List Units'), array('controller' => 'units', 'action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Unit'), array('controller' => 'units', 'action' => 'add')); ?> </li>
  </ul>
</div>
