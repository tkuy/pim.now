import {Grid, Select} from "@material-ui/core";
import TextField from "@material-ui/core/TextField";
import {translate} from "react-jhipster";
import React from "react";
import {IAttribut} from "app/shared/model/attribut.model";
import {AttributType} from "app/shared/model/enumerations/attribut-type.model";

interface Props {
  attribut:IAttribut,
  disabled: boolean,
  onChangeTextField?: (event) => void,
  onChangeSelect?: (event) => void,
}
export const AttributForm = (props:Props) => {
  const {attribut, disabled, onChangeTextField} = props;
  const [labelWidth, setLabelWidth] = React.useState(0);

  return (
    <React.Fragment>
      <Grid item xs={6} key={`attribut${attribut.id}-name`}>
        <TextField
          required
          id={"nameAttribut" + attribut.id}
          variant="outlined"
          value={attribut.nom || ''}
          disabled={disabled}
          fullWidth
          onChange={(e) => {
            onChangeTextField(e)
          }}
          label={translate("pimnowApp.attribut.nom")}
        />
      </Grid>
      <Grid item xs={6} key={`attribut${attribut.id}-type`}>
        <Select
          native
          labelWidth={labelWidth}
          id={"typeAttribut" + attribut.id}
          disabled={disabled}
          fullWidth
          onChange={(e) => {
            props.onChangeSelect(e)
          }}
          label={translate("pimnowApp.attribut.type")}
        >
          <option value=""/>
          <option value={AttributType.NUMBER} selected={attribut.type === AttributType.NUMBER }>{translate("pimnowApp.AttributType.NUMBER")}</option>
          <option value={AttributType.RESSOURCE} selected={attribut.type === AttributType.RESSOURCE }>{translate("pimnowApp.AttributType.RESSOURCE")}</option>
          <option value={AttributType.MULTIPLE_VALUE} selected={attribut.type === AttributType.MULTIPLE_VALUE }>{translate("pimnowApp.AttributType.MULTIPLE_VALUE")}</option>
          <option value={AttributType.VALUES_LIST} selected={attribut.type === AttributType.VALUES_LIST }>{translate("pimnowApp.AttributType.VALUES_LIST")}</option>
          <option value={AttributType.TEXT} selected={attribut.type === AttributType.TEXT }>{translate("pimnowApp.AttributType.TEXT")}</option>
        </Select>
      </Grid>
    </React.Fragment>
  )
};
