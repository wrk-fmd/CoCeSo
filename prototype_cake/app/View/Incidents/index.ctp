<div class="incidents index">
  <h2><?php echo __('Incidents'); ?></h2>
  <table cellpadding="0" cellspacing="0">
    <tr>
      <th><?php echo $this->Paginator->sort('id'); ?></th>
      <th><?php echo $this->Paginator->sort('created'); ?></th>
      <th><?php echo $this->Paginator->sort('modified'); ?></th>
      <th><?php echo $this->Paginator->sort('finished'); ?></th>
      <th><?php echo $this->Paginator->sort('type'); ?></th>
      <th><?php echo $this->Paginator->sort('priority'); ?></th>
      <th><?php echo $this->Paginator->sort('text'); ?></th>
      <th><?php echo $this->Paginator->sort('comment'); ?></th>
      <th><?php echo $this->Paginator->sort('status'); ?></th>
      <th class="actions"><?php echo __('Actions'); ?></th>
    </tr>
    <?php foreach ($incidents as $incident): ?>
      <tr>
        <td><?php echo h($incident['Incident']['id']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['created']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['modified']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['finished']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['type']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['priority']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['text']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['comment']); ?>&nbsp;</td>
        <td><?php echo h($incident['Incident']['status']); ?>&nbsp;</td>
        <td class="actions">
          <?php echo $this->Html->link(__('View'), array('action' => 'view', $incident['Incident']['id'])); ?>
          <?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $incident['Incident']['id'])); ?>
        </td>
      </tr>
    <?php endforeach; ?>
  </table>
  <p>
    <?php
    echo $this->Paginator->counter(array(
      'format' => __('Page {:page} of {:pages}, showing {:current} records out of {:count} total, starting on record {:start}, ending on {:end}')
    ));
    ?>	</p>
  <div class="paging">
    <?php
    echo $this->Paginator->prev('< ' . __('previous'), array(), null, array('class' => 'prev disabled'));
    echo $this->Paginator->numbers(array('separator' => ''));
    echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
    ?>
  </div>
</div>
<div class="actions">
  <h3><?php echo __('Actions'); ?></h3>
  <ul>
    <li><?php echo $this->Html->link(__('New Incident'), array('action' => 'add')); ?></li>
    <li><?php echo $this->Html->link(__('List Units'), array('controller' => 'units', 'action' => 'index')); ?> </li>
    <li><?php echo $this->Html->link(__('New Unit'), array('controller' => 'units', 'action' => 'add')); ?> </li>
  </ul>
</div>
