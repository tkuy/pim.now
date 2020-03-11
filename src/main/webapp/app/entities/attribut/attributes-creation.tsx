import React from 'react';
import {IAttribut} from "app/shared/model/attribut.model";
import {FormControl, Grid, InputLabel} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import {deepUpdate} from "immupdate/es/immupdate";
import {Translate, translate} from "react-jhipster";
import {AttributType} from "app/shared/model/enumerations/attribut-type.model";
import {makeStyles} from "@material-ui/core/styles";
import Select from "@material-ui/core/Select";
import FormHelperText from "@material-ui/core/FormHelperText";

interface Props {
  attributes: IAttribut[];
  setAttributes: (attribut: IAttribut[]) => void;
  disabled: boolean,
  editName?: boolean
}

const useStyles = makeStyles(theme => ({
  formControl: {
    width: '100%',
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  paper: {
    display: 'flex',
    flexDirection: "column" as "column",
    alignItems: 'center',
  },
  controls: {
    flexGrow: 1,
  },
}));

export const AttributesCreation = (props: Props) => {
  const inputLabel = React.useRef<HTMLLabelElement>(null);
  const [labelWidth, setLabelWidth] = React.useState(0);
  React.useEffect(() => {
    setLabelWidth(inputLabel.current && inputLabel.current.offsetWidth);
  }, []);
  const {attributes, setAttributes, disabled, editName} = props;
  const classes = useStyles();
  return (
    <React.Fragment>
      {attributes.map((attribut, index) =>
        <React.Fragment key={`attributes-creation-${attribut.id}`}>
          <Grid item xs={3} key={`attribut${attribut.id}-idF`}>
            <TextField
              id={"idFAttribut" + attribut.id}
              variant="outlined"
              value={attribut.idF || ''}
              fullWidth
              onChange={e => {
                const value = e.target.value.trim();
                const newAttribut = deepUpdate(attribut).at('idF').set(value);
                const attributesTmp = attributes.slice();
                attributesTmp[index] = newAttribut;
                setAttributes(attributesTmp);
              }}
              label={translate("pimnowApp.attribut.idF")}
              disabled={disabled}
            />
          </Grid>
          <Grid item xs={5} key={`attribut${attribut.id}-name`}>
            <TextField
              id={"nameAttribut" + attribut.id}
              variant="outlined"
              value={attribut.nom || ''}
              fullWidth
              onChange={e => {
                const value = e.target.value as AttributType;
                const newAttribut = deepUpdate(attribut).at('nom').set(value);
                const attributesTmp = attributes.slice();
                attributesTmp[index] = newAttribut;
                setAttributes(attributesTmp);
              }}
              label={translate("pimnowApp.attribut.nom")}
              disabled={disabled && !editName}
            />
          </Grid>
          <Grid item xs={4} key={`attribut${attribut.id}-type`}>
            <FormControl variant="outlined" className={classes.formControl}>
              <InputLabel ref={inputLabel} id={`input-attribut${attribut.id}-type`}>
                <Translate contentKey={"pimnowApp.attribut.type"}/>
              </InputLabel>
              <Select
                native
                id={"typeAttribut" + attribut.id}
                fullWidth
                onChange={e => {
                  const value = e.target.value as AttributType;
                  const newAttribut = deepUpdate(attribut).at('type').set(value);
                  const attributesTmp = attributes.slice();
                  attributesTmp[index] = newAttribut;
                  setAttributes(attributesTmp);
                }}
                labelWidth={labelWidth}
                disabled={disabled}
              >
                <option value=""/>
                <option value={AttributType.NUMBER}
                        selected={attribut.type === AttributType.NUMBER}>{translate("pimnowApp.AttributType.NUMBER")}</option>
                <option value={AttributType.RESSOURCE}
                        selected={attribut.type === AttributType.RESSOURCE}>{translate("pimnowApp.AttributType.RESSOURCE")}</option>
                <option value={AttributType.MULTIPLE_VALUE}
                        selected={attribut.type === AttributType.MULTIPLE_VALUE}>{translate("pimnowApp.AttributType.MULTIPLE_VALUE")}</option>
                <option value={AttributType.TEXT}
                        selected={attribut.type === AttributType.TEXT}>{translate("pimnowApp.AttributType.TEXT")}</option>
              </Select>
            </FormControl>
          </Grid>
          <FormControl key={`form-control-attribut-${attribut.id}`}>
            <FormHelperText
              id={`attribut-${attribut.id}-notCompleted`}
              key={`attribut-${attribut.id}-notCompleted`}
              hidden={(!!attribut.nom && !!attribut.idF && !!attribut.type)
              || (!attribut.nom && !attribut.idF && !attribut.type)}>
              <Translate contentKey={"pimnowApp.attribut.completeHelper"}/>
            </FormHelperText>
          </FormControl>
        </React.Fragment>
      )
      }
    </React.Fragment>
  )
}


