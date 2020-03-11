import 'react-toastify/dist/ReactToastify.css';
import './app.scss';

import React, {useEffect} from 'react';
import {connect} from 'react-redux';
import {Card} from 'reactstrap';
import {BrowserRouter as Router} from 'react-router-dom';
import {toast, ToastContainer} from 'react-toastify';
import {hot} from 'react-hot-loader';

import {IRootState} from 'app/shared/reducers';
import {getSession} from 'app/shared/reducers/authentication';
import {getProfile} from 'app/shared/reducers/application-profile';
import {setLocale} from 'app/shared/reducers/locale';
import Header from 'app/shared/layout/header/header';

import {hasAnyAuthority} from 'app/shared/auth/private-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import {AUTHORITIES} from 'app/config/constants';
import AppRoutes from 'app/routes';

import {createMuiTheme} from "@material-ui/core";
import {ThemeProvider} from '@material-ui/core/styles';

import './fonts/Quicksand-Regular.ttf';

const baseHref = document
  .querySelector('base')
  .getAttribute('href')
  .replace(/\/$/, '');

export interface IAppProps extends StateProps, DispatchProps {}

export const theme = createMuiTheme({
  palette: {
    primary: {
      light: '#245173',
      main: '#245173',
      dark: '#002884',
      contrastText: '#f3f3f3',
    },
    secondary: {
      light: '#73d5ce',
      main: '#73d5ce',
      dark: '#3ea39d',
      contrastText: '#535353',
    }
  },
  typography: {
    fontFamily: [
      'Quicksand'
    ].join(','),
  }
});

export const App = (props: IAppProps) => {
  useEffect(() => {
    props.getSession();
    props.getProfile();
  }, []);

  const paddingTop = '60px';
  return (
    <Router basename={baseHref}>
      <ThemeProvider theme={theme}>
        <div className="app-container" style={{ paddingTop }}>
          <ToastContainer position={toast.POSITION.TOP_LEFT} className="toastify-container" toastClassName="toastify-toast" />
          <ErrorBoundary>
            <Header />
          </ErrorBoundary>
          <div className="container-fluid view-container" id="app-view-container">
            <Card className="jh-card">
              <ErrorBoundary>
                <AppRoutes />
              </ErrorBoundary>
            </Card>
          </div>
        </div>
      </ThemeProvider>
    </Router>
  );
};

const mapStateToProps = ({ authentication, applicationProfile, locale }: IRootState) => ({
  currentLocale: locale.currentLocale,
  isAuthenticated: authentication.isAuthenticated,
  isAdmin: hasAnyAuthority(authentication.account.authorities, [AUTHORITIES.ADMIN]),
  ribbonEnv: applicationProfile.ribbonEnv,
  isInProduction: applicationProfile.inProduction,
  isSwaggerEnabled: applicationProfile.isSwaggerEnabled
});

const mapDispatchToProps = { setLocale, getSession, getProfile };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(hot(module)(App));
