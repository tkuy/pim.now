import React, {useEffect, useState} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Row, Table} from 'reactstrap';
import {getSortState, JhiItemCount, JhiPagination, TextFormat, Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {AUTHORITIES} from 'app/config/constants';
import {ITEMS_PER_PAGE} from 'app/shared/util/pagination.constants';
import {getUserExtras, getUsers, updateUser} from './user-management.reducer';
import {IRootState} from 'app/shared/reducers';
import {Button, Chip, IconButton, Tooltip} from "@material-ui/core";
import VisibilityIcon from "@material-ui/icons/Visibility";
import EditIcon from "@material-ui/icons/Edit";
import {AccountCircle} from "@material-ui/icons";
import PersonAddIcon from '@material-ui/icons/PersonAdd';
import PersonAddDisabledIcon from '@material-ui/icons/PersonAddDisabled';
import AddIcon from '@material-ui/icons/Add';
import {AddButton} from "app/commons/add-button";
export interface IUserManagementProps extends StateProps, DispatchProps, RouteComponentProps<{}> {
}

export const UserManagement = (props: IUserManagementProps) => {
  const [pagination, setPagination] = useState(getSortState(props.location, ITEMS_PER_PAGE));

  const getAllUsers = () => props.getUsers(pagination.activePage - 1, pagination.itemsPerPage, `${pagination.sort},${pagination.order}`);
  const getAllUserExtras = () => props.getUserExtras(pagination.activePage - 1, pagination.itemsPerPage, `${pagination.sort},${pagination.order}`);

  useEffect(() => {
    getAllUserExtras();
  }, []);

  const sortUsers = () => getAllUserExtras();

  useEffect(() => {
    sortUsers();
  }, [pagination.activePage, pagination.order, pagination.sort]);

  const sort = p => () => {
    setPagination({
      ...pagination,
      order: pagination.order === 'asc' ? 'desc' : 'asc',
      sort: p
    });
    props.history.push(`${props.location.pathname}?page=${pagination.activePage}&sort=${pagination.sort},${pagination.order}`);
  };

  const handlePagination = currentPage =>
    setPagination({
      ...pagination,
      activePage: currentPage
    });

  const toggleActive = user => () =>
    props.updateUser({
      ...user,
      activated: !user.activated
    });

  const changeLabelRoles = (role) => {
    switch (role) {
      case AUTHORITIES.ADMIN :
        return (<Translate contentKey="global.roles.admin"/>);
      case AUTHORITIES.ADMIN_FONC:
        return (<Translate contentKey="global.roles.adf"/>);
      case AUTHORITIES.USER:
        return (<Translate contentKey="global.roles.user"/>);
      default:
        return "";
    }
  };

  const {account, match, totalItems, userExtras} = props;

  const activateDeactivateUser = (userExtra) => {
    if(account.login === userExtra.user.login){
      return null;
    }
    if(userExtra.user.activated){
      return (
        <Tooltip title={<Translate contentKey="global.tooltip.deactivate"/>} arrow>
          <Link to={`${match.url}/${userExtra.user.login}/delete`}>
            <IconButton disabled={account.login === userExtra.user.login} aria-label="delete">
              <PersonAddDisabledIcon style={{fill: "#d45e37"}}/>
            </IconButton>
          </Link>
        </Tooltip>);
    } else {
      return (
        <Tooltip title={<Translate contentKey="global.tooltip.activate"/>} arrow>
        <Link to={`${match.url}/${userExtra.user.login}/activate`}>
          <IconButton disabled={account.login === userExtra.user.login} aria-label="delete">
            <PersonAddIcon style={{fill: "#75d451"}}/>
          </IconButton>
        </Link>
      </Tooltip>);
    }
  };
  return (
    <div>
      <h2 id="user-management-page-heading">
        <AccountCircle style={{marginBottom: '0.3em', fill: "#245173"}} fontSize="large"/>
        &nbsp;
        <Translate contentKey="userManagement.home.title">Users</Translate>
        <Link to={`${match.url}/new`} className="float-right">
          <AddButton content={"userManagement.home.createLabel"}/>
        </Link>
      </h2>
      <Table responsive striped>
        <thead>
        <tr>
          <th className="hand" onClick={sort('user.login')} style={{textAlign: "center"}}>
            <Translate contentKey="userManagement.login">Login</Translate>
            <FontAwesomeIcon icon="sort"/>
          </th>
          <th className="hand" onClick={sort('user.email')} style={{textAlign: "center"}}>
            <Translate contentKey="userManagement.email">Email</Translate>
            <FontAwesomeIcon icon="sort"/>
          </th>
          <th className="hand" onClick={sort('user.firstName')} style={{textAlign: "center"}}>
            <Translate contentKey="userManagement.firstName"/>
            <FontAwesomeIcon icon="sort"/>
          </th>
          <th className="hand" onClick={sort('user.lastName')} style={{textAlign: "center"}}>
            <Translate contentKey="userManagement.lastName"/>
            <FontAwesomeIcon icon="sort"/>
          </th>
          <th style={{textAlign: "center"}}>
            <Translate contentKey="userManagement.profiles.table">Profiles</Translate>
          </th>
          <th/>
          <th/>
        </tr>
        </thead>
        <tbody>
        {userExtras.map((userExtra, i) => (
            <tr id={userExtra.user.login} key={`user-${i}`}>
              <td style={{textAlign: "center"}}>{userExtra.user.login}</td>
              <td style={{textAlign: "center"}}>{userExtra.user.email}</td>
              <td style={{textAlign: "center"}}>{userExtra.user.firstName}</td>
              <td style={{textAlign: "center"}}>{userExtra.user.lastName}</td>
              <td style={{textAlign: "center"}}>
                {userExtra.user.authorities
                  ? userExtra.user.authorities.map((authority, j) => (
                    <Chip
                      key={authority}
                      label={changeLabelRoles(authority)}
                      color="primary"
                    />
                  ))
                  : null}
              </td>
              <td style={{textAlign: "center"}}>
                {userExtra.user.activated ? (<Chip key="activated" style={{border: '1px solid green', color: 'green', backgroundColor:'white'}} label={<Translate contentKey="userManagement.activated"/>}/>) : (<Chip key="deactivated" style={{border: '1px solid red', color: 'red', backgroundColor:'white'}} label={<Translate contentKey="userManagement.deactivated"/>}/>)}
              </td>
              <td className="text-right">
                <div className="btn-group flex-btn-group-container">
                  <Tooltip title={<Translate contentKey="global.tooltip.see"/>} arrow>
                    <Link to={`${match.url}/${userExtra.user.login}/${userExtra.user.id}/detail`}>
                      <IconButton aria-label="watch">
                        <VisibilityIcon style={{fill: "#245173"}}/>
                      </IconButton>
                    </Link>
                  </Tooltip>
                  <Tooltip title={<Translate contentKey="global.tooltip.edit"/>} arrow>
                    <Link to={`${match.url}/${userExtra.user.login}/${userExtra.user.id}/edit`}>
                      <IconButton aria-label="edit">
                        <EditIcon style={{fill: "#c8994b"}}/>
                      </IconButton>
                    </Link>
                  </Tooltip>
                  {activateDeactivateUser(userExtra)}
                </div>
              </td>
            </tr>))}
        </tbody>
      </Table>
      <div className={userExtras && userExtras.length > 0 ? '' : 'd-none'}>
        <Row className="justify-content-center">
          <JhiItemCount page={pagination.activePage} total={totalItems} itemsPerPage={pagination.itemsPerPage}
                        i18nEnabled/>
        </Row>
        <Row className="justify-content-center">
          <JhiPagination
            activePage={pagination.activePage}
            onSelect={handlePagination}
            maxButtons={5}
            itemsPerPage={pagination.itemsPerPage}
            totalItems={props.totalItems}
          />
        </Row>
      </div>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  totalItems: storeState.userManagement.totalItems,
  account: storeState.authentication.account,
  userExtras: storeState.userManagement.userExtras
});

const mapDispatchToProps = {getUsers, updateUser, getUserExtras};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(UserManagement);
