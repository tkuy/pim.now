import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import {Translate, translate} from 'react-jhipster';
import {NavDropdown} from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown icon="th-list" name={translate('global.menu.entities.main')} id="entity-menu">
    <MenuItem icon="asterisk" to="/entity/family">
      <Translate contentKey="global.menu.entities.family" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/attribut">
      <Translate contentKey="global.menu.entities.attribut" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/category">
      <Translate contentKey="global.menu.entities.category" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/product">
      <Translate contentKey="global.menu.entities.product" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/attribut-value">
      <Translate contentKey="global.menu.entities.attributValue" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/user-extra">
      <Translate contentKey="global.menu.entities.userExtra" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/customer">
      <Translate contentKey="global.menu.entities.customer" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/workflow">
      <Translate contentKey="global.menu.entities.workflow" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/workflow-step">
      <Translate contentKey="global.menu.entities.workflowStep" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/mapping">
      <Translate contentKey="global.menu.entities.mapping" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/association">
      <Translate contentKey="global.menu.entities.association" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/configuration-customer">
      <Translate contentKey="global.menu.entities.configurationCustomer" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/values-list">
      <Translate contentKey="global.menu.entities.valuesList" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/values-list-item">
      <Translate contentKey="global.menu.entities.valuesListItem" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/workflow-state">
      <Translate contentKey="global.menu.entities.workflowState" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/attribut-values-list">
      <Translate contentKey="global.menu.entities.attributValuesList" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/entity/prestashop-product">
      <Translate contentKey="global.menu.entities.prestashopProduct" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
