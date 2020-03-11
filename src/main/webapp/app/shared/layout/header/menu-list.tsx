import {Link} from "react-router-dom";
import Typography from "@material-ui/core/Typography";
import {ListItemIcon} from "@material-ui/core";
import {Translate} from "react-jhipster";
import MenuItem from "@material-ui/core/MenuItem";
import React from "react";


const MenuListItem = (props) => {
  return (
    <Link to={props.to} style={{ textDecoration: 'none' }}>
      <MenuItem onClick={props.callback} key={props.to}>
          <ListItemIcon>
            {props.icon}
          </ListItemIcon>
          <Typography><Translate contentKey={props.text}></Translate></Typography>
      </MenuItem>
    </Link>
  )
}

export default MenuListItem ;
