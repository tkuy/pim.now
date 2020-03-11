import * as React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import TreeItem from '@material-ui/lab/TreeItem';
import {ICategory} from "app/shared/model/category.model";
import {Box, Container} from "@material-ui/core";

const useStyles = makeStyles({
  root: {

  },
});

interface Props {
  node: ICategory,
  handlingFunction ?: Function,
  handlingFunctionSimpleClick ?: Function,
  idCategory ?: number
}

function NodeTreeComponent(props: Props) {
  const {node} = props;
  const childrenComponent = node && node.successors && node.successors.filter(x => x && x.deleted !== true).map(child => <NodeTreeComponent idCategory={props.idCategory} key={`node-tree-${child.id}`} handlingFunctionSimpleClick={props.handlingFunctionSimpleClick} handlingFunction={props.handlingFunction} node={child} />);

  let style ;

  if (childrenComponent.length === 0) {
    style = {
      marginLeft: "26px"
    };
  } else {
    style = {
      marginLeft: "0"
    };
  }

  return (
    <TreeItem key={`tree-item-${node.id}`}
              onClick={(e) => {
                props.handlingFunctionSimpleClick && props.handlingFunctionSimpleClick(e, node);
                props.handlingFunction && props.handlingFunction(node);
                e.stopPropagation();
              }}
              nodeId={''+node.id}
              label={props.idCategory ? (props.idCategory === node.id ? (<Box style={style}>{node.nom} ({node.idF}) ✓</Box>) : (<Box style={style}>{node.nom} ({node.idF})</Box>)) : <Box style={style}>{node.nom} ({node.idF})</Box>}
    >
      {childrenComponent}
    </TreeItem>
  )
}

export function CategoryTree(props: Props){
  const classes = useStyles();
  const {node} = props;
  return (
    <Box width={1}>
    <TreeView
      defaultExpanded={["category root (0)"]}
      className={classes.root}
      defaultCollapseIcon={<ExpandMoreIcon />}
      defaultExpandIcon={<ChevronRightIcon />}
    >
      {node.id && <NodeTreeComponent idCategory={props.idCategory} handlingFunctionSimpleClick={props.handlingFunctionSimpleClick} handlingFunction={props.handlingFunction} node={node}/>}
    </TreeView>
    </Box>
  )

}
