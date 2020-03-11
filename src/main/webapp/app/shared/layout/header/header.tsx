import './header.scss';
// React imports
import React from 'react';
import {Link} from "react-router-dom";
import {connect} from "react-redux";
import {Storage} from 'react-jhipster';
import LoadingBar from 'react-redux-loading-bar';
// Material imports
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import {AppBar, IconButton, Menu, MenuItem, MenuList, Toolbar, Typography} from '@material-ui/core';
import {AccountCircle, Flag, Home, Lock, LockOpen} from '@material-ui/icons';
// i18n imports
import {languages, locales} from 'app/config/translation';
// Others imports
import Sidebar from 'app/shared/layout/menus/sidebar'
import MenuListItem from "app/shared/layout/header/menu-list";
import {setLocale} from 'app/shared/reducers/locale';

export interface IHeaderProps extends StateProps {
  setLocale: Function;
  isAuthenticated: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  account: any ;
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      flexGrow: 1,
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    title: {
      flexGrow: 1,
      opacity: '100%',
    },
    right: {
      position: 'relative',
      borderRadius: theme.shape.borderRadius,
      marginLeft: theme.spacing(1),
      width: 'auto',
    },
  }),
);

const Header = (props: IHeaderProps) => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  const [anchorElLn, setAnchorElLn] = React.useState<null | HTMLElement>(null);
  const openLn = Boolean(anchorElLn);
  const handleClickLn = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElLn(event.currentTarget);
  };
  const handleCloseLn = () => {
    setAnchorElLn(null);
  };

  const handleLocaleChange = (event,local) => {
    Storage.session.set('locale', local);
    props.setLocale(local);
    handleCloseLn() ;
  };

  const classes = useStyles();

  return (
    <div id="app-header">
      <LoadingBar className="loading-bar" />
      <AppBar position="fixed">
        <Toolbar>
          <Sidebar/>
          <Typography className={classes.title} variant="h6" noWrap>
            PIM.NOW
          </Typography>
          <div className={classes.right}>

            <Link to="/">
              <IconButton
                edge="start"
                color="inherit"
                aria-label="open drawer"
                style={{color:"white",marginRight:".5em"}}
              >
                <Home />
              </IconButton>
            </Link>

            <IconButton
              edge="start"
              onClick={handleClickLn}
              color="inherit"
              aria-label="open drawer"
              style={{color:"white",marginRight:".5em"}}
            >
              <Flag />
            </IconButton>

            <Menu
              id="long-menu"
              keepMounted
              open={openLn}
              anchorEl={anchorElLn}
              onClose={handleCloseLn}
            >
              <MenuList>
                {locales.map(locale => (
                  <MenuItem onClick={(event) => handleLocaleChange(event, locale)} key={locale}>
                    <Typography>
                      {languages[locale].name}
                    </Typography>
                  </MenuItem>
                ))}
              </MenuList>
            </Menu>

            <IconButton
              edge="start"
              onClick={handleClick}
              color="inherit"
              aria-label="open drawer"
            >
              <AccountCircle />
            </IconButton>

            <Menu
              id="long-menu"
              anchorEl={anchorEl}
              keepMounted
              open={open}
              onClose={handleClose}
            >
              <MenuList>

                { props.isAuthenticated === false ?
                  (
                    <MenuListItem callback={handleClose} icon={<LockOpen />} to="/login" text="global.menu.account.login" />
                  ) : null
                }
                { props.isAuthenticated && props.account.isAdmin ?
                  (
                    null
                  ) : null
                }
                {props.isAuthenticated ?
                  (
                    <MenuListItem callback={handleClose} icon={<Lock/>} to="/logout" text="global.menu.account.logout"/>
                  ) : null
                }
              </MenuList>
            </Menu>
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
};


const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  isInProduction: storeState.isInProduction,
  ribbonEnv: storeState.ribbonEnv
});
const mapDispatchToProps = { setLocale };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps,mapDispatchToProps)(Header);
