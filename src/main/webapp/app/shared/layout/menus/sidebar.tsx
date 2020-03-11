import React from 'react';
import {createStyles, makeStyles, Theme} from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import InboxIcon from '@material-ui/icons/MoveToInbox';
import MenuIcon from "@material-ui/icons/Menu";
import {Avatar, Collapse, Drawer, Grid, IconButton, Typography} from "@material-ui/core";
import {connect} from "react-redux";
import {
  ViewModule,
  MenuOpen,
  AssignmentReturned,
  AccountCircle,
  Add,
  ExpandLess,
  ExpandMore,
  ImportExport,
  BusinessCenter,
  List as ListIcon,
  Store
} from '@material-ui/icons';
import {translate, Translate} from "react-jhipster";
import {Link} from "react-router-dom";
import {AUTHORITIES} from "app/config/constants";


const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    list: {
      width: 250,
    },
    fullList: {
      width: 250,
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    nested: {
      paddingLeft: theme.spacing(4),
    },
  }),
);

export interface ISidebarProps extends StateProps {
  isAuthenticated: boolean;
  account: any;
}

const SideBar = (props: ISidebarProps) => {
  const classes = useStyles();
  const [state, setState] = React.useState({left: false});

  const [openRoleAdmin, setOpenRoleAdmin] = React.useState(false);
  const handleClickRoleAdmin = () => {
    setOpenRoleAdmin(!openRoleAdmin);
  };

  const [openRoleAdminFonc, setOpenRoleAdminFonc] = React.useState(false);
  const handleClickRoleAdminFonc = () => {
    setOpenRoleAdminFonc(!openRoleAdminFonc);
  };

  const [openRoleUser, setOpenRoleUser] = React.useState(false);
  const handleClickRoleUser = () => {
    setOpenRoleUser(!openRoleUser);
  };

  const [openProduct, setOpenProduct] = React.useState(false);
  const handleClickProduct = () => {
    setOpenProduct(!openProduct);
  };

  const [openConfiguration, setOpenConfiguration] = React.useState(false);
  const handleClickConfiguration = () => {
    setOpenConfiguration(!openConfiguration);
  };

  const toggleDrawer = (open: boolean) => (
    event: React.KeyboardEvent | React.MouseEvent,
  ) => {
    if (
      event &&
      event.type === 'keydown' &&
      ((event as React.KeyboardEvent).key === 'Tab' ||
        (event as React.KeyboardEvent).key === 'Shift')
    ) {
      return;
    }

    setState({...state, ['left']: open});
  };


  const ROLE_USER_MENU = () => {
    return props.account.authorities.find((elt) => elt === AUTHORITIES.USER) || props.account.authorities.find((elt) => elt === AUTHORITIES.ADMIN_FONC) ? (
      <React.Fragment>
        <ListItem button onClick={handleClickProduct}>
          <ListItemIcon>
            <Store/>
          </ListItemIcon>
          <ListItemText primary={<Translate contentKey={"global.menu.sidebar.product"}/>}/>
          {openProduct ? <ExpandLess/> : <ExpandMore/>}
        </ListItem>
        <Collapse in={openProduct} timeout="auto" unmountOnExit>
          <List component="div" disablePadding>
            <Link to={"/entity/product"}>
              <ListItem button onClick={toggleDrawer(false)} className={classes.nested}>
                <ListItemIcon>
                  <ListIcon/>
                </ListItemIcon>
                <ListItemText primary={<Translate contentKey={"global.menu.sidebar.productList"}/>}/>
              </ListItem>
            </Link>
            <Link to={"/entity/product/new"}>
              <ListItem button onClick={toggleDrawer(false)} className={classes.nested}>
                <ListItemIcon>
                  <Add/>
                </ListItemIcon>
                <ListItemText primary={<Translate contentKey={"global.menu.sidebar.addProduct"}/>}/>
              </ListItem>
            </Link>
            <Link to={"/entity/import"}>
              <ListItem button onClick={toggleDrawer(false)} className={classes.nested}>
                <ListItemIcon>
                  <ImportExport/>
                </ListItemIcon>
                <ListItemText primary={<Translate contentKey={"global.menu.sidebar.importexport"}/>}/>
              </ListItem>
            </Link>
            <Link to={"/entity/mapping"}>
              <ListItem button onClick={toggleDrawer(false)} className={classes.nested}>
                <ListItemIcon>
                  <ListIcon/>
                </ListItemIcon>
                <ListItemText primary={<Translate contentKey={"global.menu.sidebar.templatesList"}/>}/>
              </ListItem>
            </Link>
          </List>
        </Collapse>
        <Link to={"/entity/family"}>
          <ListItem button onClick={toggleDrawer(false)} key="_Familles">
            <ListItemIcon><ViewModule/></ListItemIcon>
            <ListItemText primary={<Translate contentKey={"global.menu.sidebar.families"}/>} />
          </ListItem>
        </Link>
        <Link to={"/entity/category"}>
          <ListItem button onClick={toggleDrawer(false)} key="_Categories">
            <ListItemIcon><MenuOpen/></ListItemIcon>
            <ListItemText primary={<Translate contentKey={"global.menu.sidebar.categories"}/>} />
          </ListItem>
        </Link>
      </React.Fragment>
    ) : null;
  };

  const ROLE_ADMIN_MENU = () => {
    return props.account.authorities.find((elt) => elt === AUTHORITIES.ADMIN) ? (
      <React.Fragment>
        <Link to={"/entity/customer"}>
          <ListItem button onClick={toggleDrawer(false)} key="_Users">
            <ListItemIcon><BusinessCenter/></ListItemIcon>
            <ListItemText primary={<Translate contentKey={"global.menu.sidebar.customer"}/>}/>
          </ListItem>
        </Link>
        <Link to={"/admin/user-management"}>
          <ListItem button onClick={toggleDrawer(false)} key="_Users">
            <ListItemIcon><AccountCircle/></ListItemIcon>
            <ListItemText primary={<Translate contentKey={"global.menu.sidebar.users"}/>}/>
          </ListItem>
        </Link>
      </React.Fragment>
    ) : null;
  };

  const ROLE_ADMIN_FONC_MENU = () => {
    return props.account.authorities.find((elt) => elt === AUTHORITIES.ADMIN_FONC) ? (
      <React.Fragment>
        <ListItem button onClick={handleClickConfiguration}>
          <ListItemIcon>
            <AssignmentReturned/>
          </ListItemIcon>
          <ListItemText primary={<Translate contentKey={"global.menu.sidebar.configuration_integration"}/>}/>
          {openConfiguration ? <ExpandLess/> : <ExpandMore/>}
        </ListItem>
        <Collapse in={openConfiguration} timeout="auto" unmountOnExit>
          <List component="div" disablePadding>
            <Link to={"/entity/configuration-customer"}>
              <ListItem button onClick={toggleDrawer(false)} className={classes.nested}>
                <ListItemIcon>
                  <Avatar alt="Prestashop" src="../../content/images/prestashop.png"/>
                </ListItemIcon>
                <ListItemText primary={<Translate contentKey={"global.menu.sidebar.prestashop"}/>}/>
              </ListItem>
            </Link>
          </List>
        </Collapse>
        <Link to={"/admin/user-management"}>
          <ListItem button onClick={toggleDrawer(false)} key="_Users">
            <ListItemIcon><AccountCircle/></ListItemIcon>
            <ListItemText primary={<Translate contentKey={"global.menu.sidebar.users"}/>}/>
          </ListItem>
        </Link>
      </React.Fragment>
    ) : null;
  };

  return (
    <div>
      {
        props.isAuthenticated &&
        <IconButton
          edge="start"
          className={classes.menuButton}
          color="inherit"
          aria-label="open drawer"
          onClick={toggleDrawer(true)}
        >
          <MenuIcon/>
        </IconButton>
      }


      <Drawer
        anchor="left"
        open={state.left}
        onClose={toggleDrawer(false)}
      >
        <div
          className={classes.fullList}
          role="presentation"
        >
          {
            props.isAuthenticated &&
            <div>
              <Grid container alignItems={"center"} style={{
                borderBottom: "1px solid #e0e0e0",
                backgroundColor: "#245173",
                backgroundSize: "cover",
                backgroundImage: "url(../../../../content/images/bgdrawer.png)",
                backgroundRepeat: "no-repeat",
                height: "20vh",
                color: "white"
              }}>
                <Grid item xs={12} style={{textAlign: "center"}}>
                  <Typography style={{marginTop:"2em"}} variant={"subtitle1"}>
                    {props.account &&
                      <Translate contentKey="global.menu.sidebar.hello" interpolate={{
                        fname: props.account.firstName,
                        lname: props.account.lastName,
                        rank: translate("global.menu.sidebar." + props.account.authorities)
                      }}/>
                    }
                  </Typography>
                </Grid>
              </Grid>
            </div>
          }
          <List>
            {
              props.isAuthenticated ? ROLE_USER_MENU() : null
            }
            {
              props.isAuthenticated ? ROLE_ADMIN_MENU() : null
            }
            {
              props.isAuthenticated ? ROLE_ADMIN_FONC_MENU() : null
            }
          </List>
        </div>
      </Drawer>
    </div>
  );
};

const mapStateToProps = storeState => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
});
const mapDispatchToProps = {};

type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(mapStateToProps, mapDispatchToProps)(SideBar);
