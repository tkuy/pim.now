import React from 'react';
import { connect } from 'react-redux';

import { IRootState } from 'app/shared/reducers';
import { logout } from 'app/shared/reducers/authentication';
import {Translate} from "react-jhipster";
import {Typography} from "@material-ui/core";
import {RouteComponentProps} from "react-router-dom";

export interface ILogoutProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
  idToken: string;
  logoutUrl: string;
}

export class Logout extends React.Component<ILogoutProps> {
  componentDidMount() {
    this.props.logout();
    this.props.history.push('/login') ;
  }

  render() {
    const logoutUrl = this.props.logoutUrl;
    if (logoutUrl) {
      // if Keycloak, logoutUrl has protocol/openid-connect in it
      window.location.href = logoutUrl.includes('/protocol')
        ? logoutUrl + '?redirect_uri=' + window.location.origin
        : logoutUrl + '?id_token_hint=' + this.props.idToken + '&post_logout_redirect_uri=' + window.location.origin;
    }

    return (
      <div className="p-5">
        <Typography variant="h4" gutterBottom>
          <Translate contentKey={"login.logout.logout"}></Translate>
        </Typography>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  logoutUrl: storeState.authentication.logoutUrl,
  idToken: storeState.authentication.idToken
});

const mapDispatchToProps = { logout };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Logout);
