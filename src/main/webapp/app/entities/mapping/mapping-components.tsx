import {
  Checkbox,
  Container,
  Dialog, DialogActions,
  DialogContent,
  DialogTitle,
  Divider, FormControl, FormControlLabel, FormGroup, FormLabel,
  Grid,
  IconButton,
  Select,
  TextField
} from "@material-ui/core";
import {ArrowRightAlt, Delete} from "@material-ui/icons";
import React from "react";
import Typography from "@material-ui/core/Typography";
import FilterList from "@material-ui/icons/FilterList";
import {Translate} from "react-jhipster";
import Alert from "@material-ui/lab/Alert";
import Button from "@material-ui/core/Button";
import {FamilyTree} from "app/entities/family/family-tree";
import Skeleton from "@material-ui/lab/Skeleton";

export const AttributeFromExcel = (attributes, rightAttributes, isInfo) =>
  attributes.map((key, index) => (
      <Grid key={key} container spacing={2} alignItems={"center"} style={{marginBottom: "2em"}}>
        <Grid item xs={5} style={{textAlign: "center"}}>
          <Select
            disabled={isInfo}
            name={"headerFieldLeft"}
            native
            variant="outlined"
            style={{width: "100%"}}
          >
            <option key={"_none"} value="">Selectionnez ...</option>
            {attributes.map(fields => {
              return (
                <option key={fields} value={fields}>{fields}</option>
              )
            })}

          </Select>
        </Grid>
        <Grid item xs={2} style={{textAlign: "center"}}><ArrowRightAlt/></Grid>
        <Grid item xs={5} style={{textAlign: "center"}}>
          <Select
            disabled={isInfo}
            name={"headerFieldRight"}
            native
            variant="outlined"
            style={{width: "100%"}}
          >
            <option key={"_none"} value="">Selectionnez ...</option>
            {
              [...rightAttributes.keys()].map((value) => {
                return (
                  <option key={value} value={rightAttributes.get(value)}>{value}</option>
                )
              })
            }

          </Select>
        </Grid>
      </Grid>
    )
  );

export const AttributeWithField = (freeField, removeFreeField, rightAttributes, isInfo) => (
  Array.from(Array(freeField), (key, i) => (
    <Grid id={"free_attribute_" + i} key={i} container spacing={2} alignItems={"center"} style={{marginBottom: "2em"}}>
      <Grid item xs={1} style={{textAlign: "center"}}>
        <IconButton disabled={isInfo} onClick={() => removeFreeField(i)} color="secondary">
          <Delete style={{color: "red"}} />
        </IconButton>
      </Grid>
      <Grid item xs={4} style={{textAlign: "center"}}>
        <TextField disabled={isInfo} fullWidth name={"freeFieldLeft"} label="Header Excel" variant="outlined"/>
      </Grid>
      <Grid item xs={2} style={{textAlign: "center"}}><ArrowRightAlt/></Grid>
      <Grid item xs={5} style={{textAlign: "center"}}>
        <Select
          disabled={isInfo}
          name={"freeFieldRight"}
          native
          variant="outlined"
          style={{width: "100%"}}
        >
          <option key={"_none"} value="">Selectionnez ...</option>
          {
            [...rightAttributes.keys()].map((value) => {
              return (
                <option key={value} value={rightAttributes.get(value)}>{value}</option>
              )
            })
          }
        </Select>
      </Grid>
    </Grid>
  ))
);

export const AttributeDuplicated = (freeField, removeFreeField, rightAttributes, loadedAttributes, loadedAttributesRight, isInfo) => (
  [...loadedAttributes.keys()].map((value) =>
    (
      <Grid key={value} container spacing={2} alignItems={"center"} style={{marginBottom: "2em"}}>
        <Grid item xs={5} style={{textAlign: "center"}}>
          <TextField disabled={isInfo} defaultValue={value} fullWidth name={"freeFieldLeft"} label="Header Excel" variant="outlined"/>
        </Grid>
        <Grid item xs={2} style={{textAlign: "center"}}><ArrowRightAlt/></Grid>
        <Grid item xs={5} style={{textAlign: "center"}}>
          <Select
            disabled={isInfo}
            name={"freeFieldRight"}
            native
            variant="outlined"
            style={{width: "100%"}}
          >
            <option key={"_none"} value="">Selectionnez ...</option>
            {
              [...loadedAttributes.keys()].map((valueRight) => {
                return (
                  valueRight === value ?
                    <option selected={true} key={valueRight}
                            value={loadedAttributes.get(valueRight)}>{loadedAttributesRight.get(loadedAttributes.get(valueRight))}</option>
                    :
                    <option key={valueRight}
                            value={loadedAttributes.get(valueRight)}>{loadedAttributesRight.get(loadedAttributes.get(valueRight))}</option>
                )
              })
            }
          </Select>
        </Grid>
      </Grid>
    )
  )
);


export const FormFields = (errorAdd, handleChangeFile, isNew,
                           separator, setSeparator,
                           name, setName,
                           idf, setIdf,
                           description, setDescription, isInfo, isEdit) => (
  <Container maxWidth="sm">
    <Grid container spacing={4}>
      <Grid item xs={12}>
        <TextField disabled={isInfo} value={name} onChange={(e) => setName(e.target.value)} name={"name"} fullWidth label={<Translate contentKey={"pimnowApp.mapping.name"}/>}
                   variant="outlined"/>
      </Grid>
      <Grid item xs={12}>
        <TextField disabled={isInfo || isEdit} value={idf} onChange={(e) => setIdf(e.target.value)} name={"idF"} fullWidth label={<Translate contentKey={"pimnowApp.mapping.idF"}/>} variant="outlined"/>
      </Grid>
      <Divider style={{width: "100%"}}/>
      <Grid item xs={12}>
        <Translate contentKey={"pimnowApp.mapping.labelFile"}/> : <br/>
        <Grid
          container
          spacing={0}
          direction="column"
          alignItems="center"
          justify="center"
        >
          <Grid item xs={3}>
            <input disabled={isInfo} type="file" onChange={handleChangeFile}></input>
          </Grid>
        </Grid>
      </Grid>
      <Divider style={{width: "100%"}}/>

      <Grid item xs={12}>
        <TextField disabled={isInfo} value={description} onChange={(e) => setDescription(e.target.value)} name={"description"} fullWidth label={<Translate contentKey="pimnowApp.mapping.description"/>}
                   variant="outlined"/>
      </Grid>

      <Grid item xs={12}>
        <TextField disabled={isInfo} value={separator} onChange={(e) => setSeparator(e.target.value)} id={"separator"} name={"separator"}
                   fullWidth label={<Translate contentKey="pimnowApp.mapping.separator"/>} variant="outlined"/>
      </Grid>
    </Grid>
  </Container>
);

export const HeaderAttributesFields = (handleClickOpen, addFreeField, isInfo) => (
  <React.Fragment>
    <Grid
      container
      spacing={0}
      direction="row"
      alignItems="center"
      justify="flex-end"
      style={{marginTop: "2em", marginBottom: "2em"}}
    >
      <Grid item xs={6}>
        <Button disabled={isInfo} variant="contained" color="primary" onClick={handleClickOpen}>
          <Translate contentKey={"pimnowApp.mapping.rootButton"}/>
        </Button>
        &nbsp;
        <Button disabled={isInfo} variant="contained" color="secondary" onClick={addFreeField}>
          <Translate contentKey={"pimnowApp.mapping.freeAttribute"}/>
        </Button>
      </Grid>
    </Grid>

    <Grid container spacing={2} alignItems={"center"} style={{marginBottom: "2em"}}>
      <Grid item xs={5} style={{textAlign: "center"}}>
        <Translate contentKey={"pimnowApp.mapping.headerExcel"}/>
      </Grid>
      <Grid item xs={2} style={{textAlign: "center"}}>
        <ArrowRightAlt/>
      </Grid>
      <Grid item xs={5} style={{textAlign: "center"}}>
        <Translate contentKey={"pimnowApp.mapping.bdd"}/>
      </Grid>
    </Grid>
  </React.Fragment>
);

export const Modal = (handleClose,
                      open,
                      props,
                      setFamilyNameSelected,
                      setAttributesSelect,
                      setLoading,
                      loadAttributs,
                      attributesSelect,
                      rightAttributes,
                      handleCheckboxChange,
                      familyNameSelected,
                      loading, isInfo) => {
  return (
    <Dialog
      style={{minHeight: "50vh"}}
      fullWidth={true}
      open={open}
      onClose={handleClose}
    >
      <DialogTitle>
        <Translate contentKey={"pimnowApp.mapping.rootButton"}/>
      </DialogTitle>
      <DialogContent>
        {props.familyEntityRoot && props.familyEntityRoot.id &&
        <FamilyTree handlingFunction={
          (id, familyName) => {
            setFamilyNameSelected(familyName);
            setAttributesSelect([]);
            setLoading(true);
            loadAttributs(id);
          }
        }
                    attributes={attributesSelect}
                    node={props.familyEntityRoot}/>
        }
        <FormControl component="fieldset">
          {attributesSelect.length > 0 ? <FormLabel component="legend">Attributs liés à la famille :</FormLabel> : null}
          {attributesSelect.length === 0 ?
            <FormLabel component="legend">Veuillez Selectionner une famille</FormLabel> : null}
          <FormGroup>
            {attributesSelect.length > 0 && attributesSelect.map((value) => {
              return (
                <React.Fragment key={value.idF}>
                  <FormControlLabel
                    style={{margin: "0px"}}
                    control={
                      rightAttributes.has("[" + familyNameSelected + "] " + value.nom) || rightAttributes.has(value.nom)
                        ? (
                          <Checkbox defaultChecked={true} onChange={(e) => handleCheckboxChange(e, value.id, value.nom)}
                                    value={value.id}/>)
                        : (<Checkbox onChange={(e) => handleCheckboxChange(e, value.id, value.nom)} value={value.id}/>)
                    }
                    label={value.nom}
                  />
                </React.Fragment>
              )
            })}
          </FormGroup>
        </FormControl>

        {loading &&
        (
          [1].map((v) => {
            return (
              <React.Fragment key={v}>
                <Grid container spacing={2}>
                  <Grid item xs={2}>
                    <Skeleton height={30} variant="rect"/>
                  </Grid>
                  <Grid item xs={10}>
                    <Skeleton height={30} variant="rect"/>
                  </Grid>
                </Grid>
              </React.Fragment>
            )
          })

        )
        }

      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose}>
          Ok
        </Button>
      </DialogActions>
    </Dialog>
  )
};
