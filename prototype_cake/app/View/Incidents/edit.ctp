<div class="incidents form">
  <?php echo $this->Form->create('Incident'); ?>
  <fieldset>
    <legend><?php echo __('Edit Incident'); ?></legend>
    <?php
    echo $this->Form->input('id');
    echo $this->Form->input('type', array('type' => 'select', 'options' => array(1 => 'Info', 2 => 'Verlegung', 3 => 'Auftrag/Einsatz')));
    echo $this->Form->input('priority');
    echo $this->Form->input('text');
    echo $this->Form->input('comment');
    echo $this->Form->input('status', array('type' => 'select', 'options' => array(0 => 'Neu', 1 => 'Offen', 2 => 'Disponiert', 3 => 'in Arbeit', 4 => 'Abgeschlossen')));
    ?>
  </fieldset>
  <?php echo $this->Form->end(__('Submit')); ?>
</div>
<div class="actions">
  <h3><?php echo __('Actions'); ?></h3>
  <ul>
    <li><?php echo $this->Html->link(__('List Incidents'), array('action' => 'index')); ?></li>
    <li><?php echo $this->Html->link(__('List Units'), array('controller' => 'units', 'action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Unit'), array('controller' => 'units', 'action' => 'add')); ?> </li>
  </ul>
</div>
