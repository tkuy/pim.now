import React from 'react';
import { makeStyles, Theme, createStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Checkbox from '@material-ui/core/Checkbox';
import Button from '@material-ui/core/Button';
import Paper from '@material-ui/core/Paper';
import {IWorkflow} from "app/shared/model/workflow.model";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      margin: 'auto',
    },
    paper: {
      width: 200,
      height: 230,
      overflow: 'auto',
    },
    button: {
      margin: theme.spacing(0.5, 0),
    },
  }),
);

interface Props {
  left: IWorkflow[],
  setLeft: (left:IWorkflow[]) => void,
  right: IWorkflow[]
  setRight: (right:IWorkflow[]) => void,
}

function not(a: IWorkflow[], b: IWorkflow[]) {
  return a.filter(value => !b.includes(value));
}

function intersection(a: IWorkflow[], b: IWorkflow[]) {
  return a.filter(value => b.includes(value));
}

export const TransferListWorkflow = (props: Props) => {
  const classes = useStyles();
  const [checked, setChecked] = React.useState<IWorkflow[]>([]);
  const {left, right} = props;

  const leftChecked = intersection(checked, left);
  const rightChecked = intersection(checked, right);


  const handleToggle = (value: IWorkflow) => () => {
    // eslint-disable-next-line
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }
    setChecked(newChecked);
  };

  const handleAllRight = () => {
    props.setRight(right.concat(left));
    props.setLeft([]);
  };

  const handleCheckedRight = () => {
    props.setRight(right.concat(leftChecked));
    props.setLeft(not(left, leftChecked));
    setChecked(not(checked, leftChecked));
  };

  const handleCheckedLeft = () => {
    props.setLeft(left.concat(rightChecked));
    props.setRight(not(right, rightChecked));
    setChecked(not(checked, rightChecked));
  };

  const handleAllLeft = () => {
    props.setLeft(left.concat(right));
    props.setRight([]);
  };

  const customList = (items: IWorkflow[]) => (
    <Paper className={classes.paper}>
      <List dense component="div" role="list">
        {items.map((value: IWorkflow) => {
          const labelId = `transfer-list-item-${value}-label`;

          return (
            <ListItem key={`wkflw-value-${value.id}`} role="listitem" button onClick={handleToggle(value)}>
              <ListItemIcon>
                <Checkbox
                  checked={checked.includes(value)}
                  tabIndex={-1}
                  disableRipple
                  inputProps={{ 'aria-labelledby': labelId }}
                />
              </ListItemIcon>
              <ListItemText id={labelId} primary={`${value.name} (${value.idF})`} />
            </ListItem>
          );
        })}
        <ListItem />
      </List>
    </Paper>
  );

  return (
    <Grid container spacing={2} justify="center" alignItems="center" className={classes.root}>
      <Grid item>{customList(left)}</Grid>
      <Grid item>
        <Grid container direction="column" alignItems="center">
          <Button
            variant="outlined"
            size="small"
            className={classes.button}
            onClick={handleAllRight}
            disabled={left.length === 0}
            aria-label="move all right"
          >
            ≫
          </Button>
          <Button
            variant="outlined"
            size="small"
            className={classes.button}
            onClick={handleCheckedRight}
            disabled={leftChecked.length === 0}
            aria-label="move selected right"
          >
            &gt;
          </Button>
          <Button
            variant="outlined"
            size="small"
            className={classes.button}
            onClick={handleCheckedLeft}
            disabled={rightChecked.length === 0}
            aria-label="move selected left"
          >
            &lt;
          </Button>
          <Button
            variant="outlined"
            size="small"
            className={classes.button}
            onClick={handleAllLeft}
            disabled={right.length === 0}
            aria-label="move all left"
          >
            ≪
          </Button>
        </Grid>
      </Grid>
      <Grid item>{customList(right)}</Grid>
    </Grid>
  );
}
