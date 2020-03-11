import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {RouteComponentProps} from 'react-router-dom';
import {Button, Modal, ModalBody, ModalFooter, ModalHeader} from 'reactstrap';
import {Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {deleteUser, getUser, activateUser} from './user-management.reducer';
import {IRootState} from 'app/shared/reducers';
import PersonAddIcon from '@material-ui/icons/PersonAdd';

export interface IUserManagementActivateDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ login: string }> {}

export const UserManagementActivateDialog = (props: IUserManagementActivateDialogProps) => {
  useEffect(() => {
    props.getUser(props.match.params.login);
  }, []);

  const handleClose = event => {
    event.stopPropagation();
    props.history.goBack();
  };

  const confirmActivate = event => {
    props.activateUser(props.user.id);
    handleClose(event);
  };

  const { user } = props;

  return (
    <Modal isOpen style={{marginTop:'10em'}} toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="userManagement.activate.title"/>
      </ModalHeader>
      <ModalBody>
        <Translate contentKey="userManagement.activate.question" interpolate={{ login: user.login }}/>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel"/>
        </Button>
        <Button color="success" onClick={confirmActivate}>
          <PersonAddIcon style={{fill:'#75d451'}} />
          &nbsp;
          <Translate contentKey="entity.action.activate"/>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  user: storeState.userManagement.user
});

const mapDispatchToProps = { getUser, deleteUser, activateUser };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(UserManagementActivateDialog);
