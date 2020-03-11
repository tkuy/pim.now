import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';

import {IRootState} from 'app/shared/reducers';
import {getEntity} from './product.reducer';
import {Button, Container, Divider, Grid, Typography} from "@material-ui/core";
import {Translate, translate} from "react-jhipster";
import Skeleton from "@material-ui/lab/Skeleton";
import EditIcon from "@material-ui/icons/Edit";
import {AttributType} from "app/shared/model/enumerations/attribut-type.model";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

import {filesLocation} from '../../../config/vps-config'
import {STANDARD_SEPARATOR} from "app/config/constants";

export interface IProductDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {
}

export class ProductDetail extends React.Component<IProductDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
    this.line = this.line.bind(this);
    this.multipleLine = this.multipleLine.bind(this);
  }

  line(text: string, value: string) {
    return (
      value ? <>{text} : {value}</> : <Skeleton width={350} height={25} variant={"rect"} animation={"wave"}/>
    )
  } ;

  attributeWithRessource(text: string, value: string) {
    return (
      value ? <>{text} : <a target="_file" href={filesLocation + value}>{value}</a></> : <Skeleton width={350} height={25} variant={"rect"} animation={"wave"}/>
    )
  } ;

  multipleLine(text, categories) {
    return (
      categories ? categories.length > 1 ?
          <>{text} : {categories.reduce((prev, curr) => ['(' + prev.idF + ') ' + prev.nom, ' - ', '(' + curr.idF + ') ' +curr.nom])}</> : <>{text} : {'(' + categories[0].idF + ') ' + categories[0].nom}</>
         : <Skeleton width={350} height={25} variant={"rect"} animation={"wave"}/>
    );
  }

  render() {
    const {productEntity} = this.props;
    return (
      <>
        <Container maxWidth={"md"}>

          <Grid container>
            <Grid item xs={8}>
              <Typography variant={"h5"}><Translate contentKey={"pimnowApp.product.detail"}/></Typography>
            </Grid>
          </Grid>
          <Divider style={{width: "100%", backgroundColor: "black", marginBottom: "2em"}}/>
          <Grid container>
            <Grid item xs={8}>
              <p>{productEntity.idF && this.line(translate("pimnowApp.product.idF"), productEntity.idF)}</p>
              <p>{this.line(translate("pimnowApp.product.nom"), productEntity.nom)}</p>
              <p>{this.line(translate("pimnowApp.product.description"), productEntity.description)}</p>
              <p>{this.multipleLine(translate("pimnowApp.product.categories"), productEntity.categories)}</p>
              <p>{this.line(translate("pimnowApp.product.families"), productEntity.family ? '(' + productEntity.family.idF + ') ' +  productEntity.family.nom : undefined)}</p>
            </Grid>
            <Grid xs={4}>
              {productEntity.id ?
                (<Link to={"/entity/product/" + productEntity.id + "/edit"}>
                    <Button style={{marginTop: '1em'}} variant="contained" size="medium" color="primary">
                      <EditIcon/>
                      &nbsp;
                      <Translate contentKey="pimnowApp.product.home.editProduct"/>
                    </Button>
                  </Link>
                ) :
                (
                  <Skeleton width={190} height={36} variant={"rect"} animation={"wave"}/>
                )
              }
            </Grid>
            <Typography variant={"h5"}><Translate contentKey={"pimnowApp.product.features"}/></Typography>
            <Divider style={{width: "100%", backgroundColor: "black", marginBottom: "2em"}}/>
            <Grid item xs={8}>
              {
                productEntity.attributValues && productEntity.attributValues.map((k, v) => {
                  if (k.attribut.type === AttributType.MULTIPLE_VALUE) {
                    const nom = '(' + k.attribut.idF + ') ' + '' + k.attribut.nom;
                    return (<p>{this.line(nom, k.value.replace(/Ø/gi, ", "))}</p>) ;
                  } else if(k.attribut.type === AttributType.RESSOURCE) {
                    const nom = '(' + k.attribut.idF + ') ' + '' + k.attribut.nom;
                    return <p>{this.attributeWithRessource(nom, k.value)}</p>
                  } else  {
                    const nom = '(' + k.attribut.idF + ') ' + '' + k.attribut.nom;
                    return <p>{this.line(nom, k.value)}</p>
                  }
                })
              }
              {
                !productEntity.attributValues && <Skeleton width={350} height={25} variant={"rect"} animation={"wave"}/>
              }
            </Grid>
          </Grid>
        <Grid container justify="flex-end" >
          <Grid item xs={4} style={{textAlign:"center"}}>
            <Link to={"/entity/product"}>
              <Button
                color="secondary"
                variant="contained"
              >
                <FontAwesomeIcon icon="arrow-left"/>
                &nbsp;
                <Translate contentKey="entity.action.back">Back</Translate>
              </Button>
            </Link>
          </Grid>
        </Grid>
        </Container>
      </>
    );
  }

}

const mapStateToProps = ({product}: IRootState) => ({
  productEntity: product.entity
});

const mapDispatchToProps = {getEntity};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ProductDetail);
