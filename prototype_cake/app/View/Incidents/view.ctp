<div class="incidents view">
  <h2><?php echo __('Incident'); ?></h2>
  <dl>
    <dt><?php echo __('Id'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['id']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Created'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['created']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Modified'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['modified']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Finished'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['finished']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Type'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['type']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Priority'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['priority']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Text'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['text']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Comment'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['comment']); ?>
      &nbsp;
    </dd>
    <dt><?php echo __('Status'); ?></dt>
    <dd>
      <?php echo h($incident['Incident']['status']); ?>
      &nbsp;
    </dd>
  </dl>
</div>
<div class="actions">
  <h3><?php echo __('Actions'); ?></h3>
  <ul>
    <li><?php echo $this->Html->link(__('Add Unit'), array('controller' => 'incidents_units', 'action' => 'add', 'incident_id' => $incident['Incident']['id'], 'return' => 'incident')); ?> </li>
    <li><?php echo $this->Html->link(__('Edit Incident'), array('action' => 'edit', $incident['Incident']['id'])); ?> </li>
    <li><?php echo $this->Html->link(__('List Incidents'), array('action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Incident'), array('action' => 'add')); ?> </li>
    <li><?php echo $this->Html->link(__('List Units'), array('controller' => 'units', 'action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Unit'), array('controller' => 'units', 'action' => 'add')); ?> </li>
  </ul>
</div>
<div class="related">
  <h3><?php echo __('Related Units'); ?></h3>
  <?php if (!empty($incident['Unit'])): ?>
    <table cellpadding = "0" cellspacing = "0">
      <tr>
        <th><?php echo __('Name'); ?></th>
        <th><?php echo __('Status'); ?></th>
        <th><?php echo __('Last change'); ?></th>
      </tr>
      <?php
      foreach ($incident['Unit'] as $unit):
        ?>
        <tr>
          <td><?php echo $this->Html->link($unit['name'], array('controller' => 'units', 'action' => 'view', $unit['id'])); ?></td>
          <td><?php echo $this->Html->link($unit['IncidentsUnit']['status'], array('controller' => 'incidents_units', 'action' => 'edit', 'return' => 'incident', $unit['IncidentsUnit']['id'])); ?></td>
          <td><?php echo $unit['IncidentsUnit']['modified']; ?></td>
        </tr>
      <?php endforeach; ?>
    </table>
  <?php endif; ?>

</div>
