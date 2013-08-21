<?php
/**
 *
 * PHP 5
 *
 * CakePHP(tm) : Rapid Development Framework (http://cakephp.org)
 * Copyright 2005-2012, Cake Software Foundation, Inc. (http://cakefoundation.org)
 *
 * Licensed under The MIT License
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright 2005-2012, Cake Software Foundation, Inc. (http://cakefoundation.org)
 * @link          http://cakephp.org CakePHP(tm) Project
 * @package       Cake.View.Layouts
 * @since         CakePHP(tm) v 0.10.0.1076
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
?>
<!DOCTYPE HTML>
<html>
  <head>
    <?php echo $this->Html->charset(); ?>
    <title>
      <?php echo $title_for_layout; ?> -
      <?php echo __('CoCeSo'); ?>
    </title>
    <?php
    echo $this->Html->meta('icon');

    echo $this->Html->css('cake.generic');
    echo $this->Html->css('coceso');

    echo $this->fetch('meta');
    echo $this->fetch('css');
    echo $this->fetch('script');
    ?>
  </head>
  <body>
    <div id="container">
      <div id="header">
        <h1><?php echo $this->Html->link('CoCeSo', array('controller' => 'units', 'action' => 'index')); ?></h1>
        <ul id="navbar">
          <li><?php echo $this->Html->link('Units', array('controller' => 'units', 'action' => 'index')); ?></li>
          <li><?php echo $this->Html->link('Incidents', array('controller' => 'incidents', 'action' => 'index')); ?></li>
        </ul>
      </div>
      <div id="content">
        <?php echo $this->Session->flash(); ?>
        <?php echo $this->fetch('content'); ?>
      </div>
      <div id="footer">
        &copy; WRK\Daniel Rohr 2013
      </div>
    </div>
    <?php echo $this->element('sql_dump'); ?>
<?php echo $this->Js->writeBuffer(); ?>
  </body>
</html>
