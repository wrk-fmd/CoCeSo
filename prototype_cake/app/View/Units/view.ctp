<div class="units view">
  <h2><?php echo __('Unit'); ?></h2>
  <dl>
    <dt><?php echo __('Id'); ?></dt>
    <dd>
      <?php echo h($unit['Unit']['id']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Name'); ?></dt>
    <dd>
      <?php echo h($unit['Unit']['name']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Short'); ?></dt>
    <dd>
      <?php echo h($unit['Unit']['short']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Type'); ?></dt>
    <dd>
      <?php echo h($unit['Unit']['type']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Status'); ?></dt>
    <dd>
      <?php echo h($unit['Unit']['status']); ?>
      &nbsp;
    </dd>
  </dl>
</div>
<div class="actions">
  <h3><?php echo __('Actions'); ?></h3>
  <ul>
    <li><?php echo $this->Html->link(__('Add Incident'), array('controller' => 'incidents_units', 'action' => 'add', 'unit_id' => $unit['Unit']['id'], 'return' => 'unit')); ?> </li>
    <li><?php echo $this->Html->link(__('Edit Unit'), array('action' => 'edit', $unit['Unit']['id'])); ?> </li>
    <li><?php echo $this->Html->link(__('List Units'), array('action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Unit'), array('action' => 'add')); ?> </li>
    <li><?php echo $this->Html->link(__('List Incidents'), array('controller' => 'incidents', 'action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Incident'), array('controller' => 'incidents', 'action' => 'add')); ?> </li>
  </ul>
</div>
<div class="related">
  <h3><?php echo __('Related Incidents'); ?></h3>
  <?php if (!empty($unit['Incident'])): ?>
    <table cellpadding = "0" cellspacing = "0">
      <tr>
        <th><?php echo __('Incident'); ?></th>
        <th><?php echo __('Status'); ?></th>
        <th><?php echo __('Last change'); ?></th>
      </tr>
      <?php
      foreach ($unit['Incident'] as $incident):
        ?>
        <tr>
          <td><?php echo $this->Html->link($incident['id'], array('controller' => 'incidents', 'action' => 'view', $incident['id'])); ?></td>
          <td><?php echo $this->Html->link($incident['IncidentsUnit']['status'], array('controller' => 'incidents_units', 'action' => 'edit', 'return' => 'unit', $incident['IncidentsUnit']['id'])); ?></td>
          <td><?php echo $incident['IncidentsUnit']['modified']; ?></td>
        </tr>
      <?php endforeach; ?>
    </table>
<?php endif; ?>

</div>
