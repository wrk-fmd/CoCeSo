<div class="units form">
  <?php echo $this->Form->create('Unit'); ?>
  <fieldset>
    <legend><?php echo __('Edit Unit'); ?></legend>
    <?php
    echo $this->Form->input('id');
    echo $this->Form->input('name');
    echo $this->Form->input('short');
    echo $this->Form->input('type', array('type' => 'select', 'options' => array('Trupp' => 'Trupp', 'KFZ' => 'KFZ')));
    echo $this->Form->input('status', array('type' => 'select', 'options' => array(0 => 'AD', 1 => 'NEB', 2 => 'EB', 3 => 'Bereitschaft')));
    ?>
  </fieldset>
  <?php echo $this->Form->end(__('Submit')); ?>
</div>
<div class="actions">
  <h3><?php echo __('Actions'); ?></h3>
  <ul>
    <li><?php echo $this->Html->link(__('List Units'), array('action' => 'index')); ?></li>
    <li><?php echo $this->Html->link(__('List Incidents'), array('controller' => 'incidents', 'action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Incident'), array('controller' => 'incidents', 'action' => 'add')); ?> </li>
  </ul>
</div>
