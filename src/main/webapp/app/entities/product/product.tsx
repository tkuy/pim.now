import React, {createRef} from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Col, InputGroup, Row, Table} from 'reactstrap';
import {AvForm, AvGroup, AvInput} from 'availity-reactstrap-validation';
import {
  getSortState,
  IPaginationBaseState,
  JhiItemCount,
  JhiPagination,
  size,
  Translate,
  translate
} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import ShoppingCartIcon from '@material-ui/icons/ShoppingCart';
import CancelIcon from '@material-ui/icons/Cancel';
import BackupIcon from '@material-ui/icons/Backup';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import {IRootState} from 'app/shared/reducers';
import HelpIcon from '@material-ui/icons/Help';
import {
  getEntities,
  getSearchEntities,
  integrateToPrestashop,
  removeIntegrationFromPrestashop,
  selectAll,
  getSearchEntitiesForSelectAll
} from './product.reducer';
import {ITEMS_PER_PAGE} from 'app/shared/util/pagination.constants';
import AddIcon from '@material-ui/icons/Add';
import {
  Avatar,
  Button,
  Checkbox,
  ClickAwayListener, Divider, FormControl, FormHelperText,
  Grow,
  IconButton, MenuItem,
  MenuList,
  Paper,
  Popper, Tooltip, Typography
} from "@material-ui/core";
import VisibilityIcon from "@material-ui/icons/Visibility";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import SearchIcon from "@material-ui/icons/Search";
import NotInterestedIcon from "@material-ui/icons/NotInterested";
import {IProduct} from "app/shared/model/product.model";
import Skeleton from "@material-ui/lab/Skeleton";
import {WaitingImportIntegration} from "app/entities/import/dialog_waiting_integration";
import ExportDialog from "app/entities/product/dialog_export";
import {WaitingImportIntegrationRemove} from "app/entities/product/dialog_waiting_integration_remove";
import {AUTHORITIES, MAXIMUM_INTEGRATION_NUMBER} from "app/config/constants";
import {AddButton} from "app/commons/add-button";

export interface IProductProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {
}

export interface IProductState extends IPaginationBaseState {
  search: string;
  openModalCategories: boolean;
  openMenuListIntegration: boolean;
  productSelected: IProduct[];
  disabled: boolean;
  checkList: Map<number, boolean>;
  loading: boolean;
  isAnyProductSelected: boolean;
  anchorRef: any;
  openExportModal: boolean;
  isAllSelectChecked: boolean;
  isUser: boolean;
}

export class Product extends React.Component<IProductProps, IProductState> {
  state: IProductState = {
    search: '',
    openMenuListIntegration: false,
    openModalCategories: false,
    ...getSortState(this.props.location, ITEMS_PER_PAGE),
    productSelected: [] as IProduct[],
    disabled: false,
    checkList: new Map(),
    loading: false,
    isAnyProductSelected: false,
    anchorRef: createRef(),
    openExportModal: false,
    isAllSelectChecked: false,
    isUser: false
  };

  componentDidMount() {
    if(this.props.account.authorities[0] === AUTHORITIES.USER){
      this.setState({isUser: true});
    }
    this.getEntities();
  }

  handleChange = (id: number) => (event: React.ChangeEvent<HTMLInputElement>) => {
    const newMap = this.state.checkList;
    newMap.set(id, event.target.checked);
    this.setState({checkList: newMap});
    if (this.state.isAllSelectChecked) {
      this.setState({isAllSelectChecked: false});
    }
    if (event.target.checked) {
      this.state.productSelected.push(this.props.productList.find(e => e.idF === event.target.id));
    } else {
      const saved = this.state.productSelected.find(e => e.idF === event.target.id);
      this.state.productSelected.splice(this.state.productSelected.indexOf(saved), 1);
    }
    for (const k of this.state.checkList.values()) {
      if (k) {
        this.setState({isAnyProductSelected: true});
        break;
      }
      this.setState({isAnyProductSelected: false});
    }
  };

  search = () => {
    if (this.state.search) {
      this.setState({activePage: 1}, () => {
        const {activePage, itemsPerPage, sort, order, search} = this.state;
        this.props.getSearchEntities(search, activePage - 1, itemsPerPage, `${sort},${order}`);
      });
    }
  };

  clear = () => {
    this.setState({search: '', activePage: 1}, () => {
      this.props.getEntities();
    });
  };

  componentDidUpdate(prevProps: Readonly<IProductProps>, prevState: Readonly<IProductState>, snapshot?: any): void {
    if (prevProps.productList !== this.props.productList){
      this.setState({loading: false});
    }

    if (prevProps.selectAllList !== this.props.selectAllList) {
      const newMap = this.state.checkList;
      this.props.selectAllList.forEach(p => {
        newMap.set(p.id, true);
        if (this.state.productSelected.find(e => e.idF === p.idF) === undefined) {
          this.state.productSelected.push(p);
        }
      });
      this.setState({checkList: newMap});
    }

    if(prevProps.selectAllListWithSearch !== this.props.selectAllListWithSearch){
      const newMap = this.state.checkList;
      this.props.selectAllListWithSearch.forEach(p => {
        newMap.set(p.id, true);
        if (this.state.productSelected.find(e => e.idF === p.idF) === undefined) {
          this.state.productSelected.push(p);
        }
      });
      this.setState({checkList: newMap});
    }
  }

  handleSearch = event => this.setState({search: event.target.value});

  sort = prop => () => {
    if (this.state.search === '') {
      this.setState(
        {
          order: this.state.order === 'asc' ? 'desc' : 'asc',
          sort: prop,
        },
        () => this.sortEntities()
      );
    }
  };

  sortEntities() {
    this.getEntities();
    this.props.history.push(`${this.props.location.pathname}?page=${this.state.activePage}&sort=${this.state.sort},${this.state.order}`);
  }

  handlePagination = activePage => {
    this.setState({loading: true});
    this.setState({activePage}, () => this.sortEntities());
  };

  getEntities = () => {
    const {activePage, itemsPerPage, sort, order, search} = this.state;
    this.props.getEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
  };

  handleClickIntegrateProducts = () => {
    this.props.integrateToPrestashop(this.state.productSelected);
  };

  handleClickRemoveIntegrateProducts = () => {
    this.props.removeIntegrationFromPrestashop(this.state.productSelected);
  };

  isChecked = (product) => {
    return this.state.productSelected.find(e => e.id === product.id) !== undefined;
  };

  stringCategories = (product) => {
    if (product.categories.length === 0) {
      return '';
    }
    return product.categories.map(value =>
      <Link key={`category-list-${value.id}`}
            to={`category`}>({value.idF}) {value.nom}</Link>).reduce((prev, curr) => [prev, ' - ', curr]);
  };

  handleCloseMenuList = () => {
    this.setState({openMenuListIntegration: false});
  };

  handleToggle = () => {
    this.setState({openMenuListIntegration: !this.state.openMenuListIntegration});
  };

  handleCheckAll = (event) => {
    if (!this.state.isAllSelectChecked) {
      this.setState({isAllSelectChecked: true});
      this.setState({isAnyProductSelected: true});
      if(this.state.search === '' || this.state.search === undefined){
        this.props.selectAll(0);
      } else {
        this.props.getSearchEntitiesForSelectAll(this.state.search, this.state.activePage);
      }
    }
    if (this.state.isAllSelectChecked) {
      this.setState({isAllSelectChecked: false});
      this.setState({checkList: new Map()});
      this.setState({productSelected: []});
      this.setState({isAnyProductSelected: false});
    }
  };

  tooltipLabel = () => {
    if (this.state.isAllSelectChecked) {
      return (<Translate contentKey="pimnowApp.product.deselectAll"/>);
    } else {
      return (<Translate contentKey="pimnowApp.product.selectAll"/>);
    }
  };

  skeletonLines = () => (
    <tr>
      <td>
        <Checkbox
          disabled
        />
      </td>
      <td><Skeleton animation="wave"/></td>
      <td><Skeleton animation="wave"/></td>
      <td><Skeleton animation="wave"/></td>
      <td><Skeleton animation="wave"/></td>
      <td><Skeleton animation="wave"/></td>
      <td className="text-right">
        <div className="btn-group flex-btn-group-container">
          <IconButton aria-label="watch">
            <VisibilityIcon style={{fill: "#245173"}}/>
          </IconButton>
          <IconButton aria-label="edit">
            <EditIcon style={{fill: "#c8994b"}}/>
          </IconButton>
          <IconButton aria-label="delete">
            <DeleteIcon style={{fill: "#d45e37"}}/>
          </IconButton>
        </div>
      </td>
    </tr>
  );

  render() {
    const {productList, match, totalItems, loadingIntegration, loadingIntegrationRemove} = this.props;
    return (
      <div>
        <h2 id="product-heading">
          <ShoppingCartIcon style={{fill: "#245173", marginBottom: '0.2em'}} fontSize="large"/>
          &nbsp;
          <Translate contentKey="pimnowApp.product.home.title">Products</Translate>
          {loadingIntegration ? (<WaitingImportIntegration/>) : null}
          {loadingIntegrationRemove ? <WaitingImportIntegrationRemove/> : null}
          {this.state.isAnyProductSelected ? (
            <React.Fragment>
              <div className="float-right">
                <ExportDialog data={this.state.productSelected}/>
              </div>
              <div className="float-right">
                <Button
                  aria-controls={this.state.openMenuListIntegration ? 'menu-list-grow' : undefined}
                  aria-haspopup="true"
                  onClick={this.handleToggle}
                  style={{margin: '1em', marginTop: '0em', borderColor: '#5b82ff', color: '#5b82ff'}}
                  variant="outlined"
                  size="medium"
                  ref={this.state.anchorRef}>
                  <Translate contentKey="pimnowApp.product.home.integrationTitle"/>
                  &nbsp;
                  <ArrowDropDownIcon style={{fill: '#5b82ff'}}/>
                </Button>
                <Popper style={{zIndex: 9999}} open={this.state.openMenuListIntegration}
                        anchorEl={this.state.anchorRef.current} role={undefined} transition disablePortal>
                  {({TransitionProps, placement}) => (
                    <Grow {...TransitionProps}
                          style={{transformOrigin: placement === 'bottom' ? 'center top' : 'center bottom'}}>
                      <Paper>
                        <ClickAwayListener onClickAway={this.handleCloseMenuList}>
                          <MenuList autoFocusItem={this.state.openMenuListIntegration} id="menu-list-grow">
                            <div style={{display: 'flex', alignItems: 'center', justifyContent: 'center'}}><MenuItem
                              style={{marginRight: 'auto', marginLeft: 'auto', float: 'none'}} disabled={true}><Avatar
                              alt="Prestashop" src="../../content/images/prestashop.png"/>&nbsp;Prestashop</MenuItem>
                            </div>
                            <Divider style={{width: "100%"}}/>
                            {this.state.productSelected.length > MAXIMUM_INTEGRATION_NUMBER ? (
                                <React.Fragment>
                                  <Tooltip style={{zIndex: 10000}} title={this.state.productSelected.length > MAXIMUM_INTEGRATION_NUMBER ? (
                                    <Translate contentKey="pimnowApp.product.home.cannotIntegrate"/>) : null} arrow>
                              <span>
                            <MenuItem component="div"
                                      disabled={this.state.productSelected.length > MAXIMUM_INTEGRATION_NUMBER}
                                      onClick={this.handleClickIntegrateProducts} style={{
                              border: '1px solid #245173',
                              borderRadius: '1em',
                              marginTop: '0.5em'
                            }}><BackupIcon style={{fill: '#245173'}}/>&nbsp;
                              <div style={{color: '#245173'}}><Translate
                                contentKey="pimnowApp.product.home.integrateSelection"/></div>
                            </MenuItem>
                                </span>
                                  </Tooltip>
                                  <Tooltip style={{zIndex: 10000}} title={this.state.productSelected.length > MAXIMUM_INTEGRATION_NUMBER ? (
                                    <Translate contentKey="pimnowApp.product.home.cannotIntegrateSupp"/>) : null} arrow>
                              <span>
                              <MenuItem disabled={this.state.productSelected.length > MAXIMUM_INTEGRATION_NUMBER}
                                        onClick={this.handleClickRemoveIntegrateProducts} style={{
                                border: '1px solid #d45e37',
                                marginTop: '0.2em',
                                borderRadius: '1em'
                              }}><CancelIcon style={{fill: '#d45e37'}}/>&nbsp;
                                <div style={{color: '#d45e37'}}><Translate
                                  contentKey="pimnowApp.product.home.removeIntegrateSelection"/></div>
                              </MenuItem>
                              </span>
                                  </Tooltip>
                                </React.Fragment>) :
                              <React.Fragment>
                                <MenuItem component="div"
                                          disabled={this.state.productSelected.length > MAXIMUM_INTEGRATION_NUMBER}
                                          onClick={this.handleClickIntegrateProducts} style={{
                                  border: '1px solid #245173',
                                  borderRadius: '1em',
                                  marginTop: '0.5em'
                                }}><BackupIcon style={{fill: '#245173'}}/>&nbsp;
                                  <div style={{color: '#245173'}}><Translate
                                    contentKey="pimnowApp.product.home.integrateSelection"/></div>
                                </MenuItem>
                                <MenuItem disabled={this.state.productSelected.length > MAXIMUM_INTEGRATION_NUMBER}
                                          onClick={this.handleClickRemoveIntegrateProducts} style={{
                                  border: '1px solid #d45e37',
                                  marginTop: '0.2em',
                                  borderRadius: '1em'
                                }}><CancelIcon style={{fill: '#d45e37'}}/>&nbsp;
                                  <div style={{color: '#d45e37'}}><Translate
                                    contentKey="pimnowApp.product.home.removeIntegrateSelection"/></div>
                                </MenuItem>
                              </React.Fragment>}
                          </MenuList>
                        </ClickAwayListener>
                      </Paper>
                    </Grow>
                  )}
                </Popper>
              </div>
            </React.Fragment>
          ) : null}
          <Link to={`${match.url}/new`} className="float-right">
            <AddButton content={"pimnowApp.product.home.createLabel"}/>
          </Link>
        </h2>
        <Row>
          <Col sm="12">
            <AvForm onSubmit={this.search}>
              <AvGroup>
                <InputGroup>
                  <Tooltip
                    title={
                      <React.Fragment>
                        <Translate contentKey={"pimnowApp.product.searchHelper"}/>
                      </React.Fragment>
                    }
                  >
                    <HelpIcon style={{fill:"#245173", marginTop:'0.25em', marginLeft:'-0.5em', marginRight:'0.5em'}}/>
                  </Tooltip>
                  <AvInput
                    type="text"
                    name="search"
                    value={this.state.search}
                    onChange={this.handleSearch}
                    placeholder={translate('pimnowApp.product.home.search')}
                    style={{borderRadius: '0.5em'}}
                  />
                  <Button onClick={this.search} className="input-group-addon">
                    <SearchIcon style={{fill: "#245173"}}/>
                  </Button>
                  <Tooltip title="cancel research">
                  <Button type="reset" className="input-group-addon" onClick={this.clear}>
                    <NotInterestedIcon style={{fill: "#d45e37"}} fontSize="small"/>
                  </Button>
                  </Tooltip>
                </InputGroup>
              </AvGroup>
            </AvForm>
          </Col>
        </Row>
        <div className="table-responsive">
          <Typography variant="overline" display="block" gutterBottom><Translate
            interpolate={{number: this.state.productSelected.length}}
            contentKey="pimnowApp.product.numberProduct"/></Typography>
          {productList && productList.length > 0 ? (
            <Table responsive aria-describedby="product-heading">
              <thead>
              <tr>
                <th><Tooltip title={this.tooltipLabel()} arrow
                             placement="top"><Checkbox id="selectAllCheckBox" onClick={this.handleCheckAll}
                                                       style={{marginBottom: '-0.4em'}}
                                                       checked={this.state.isAllSelectChecked}/></Tooltip></th>
                <th className={this.state.search === '' ? ("hand") : null} onClick={this.sort('idF')}>
                  <b><Translate contentKey="pimnowApp.product.idF"/></b>&nbsp;
                  {this.state.search === '' ? (<FontAwesomeIcon icon="sort"/>) : null}
                </th>
                <th className={this.state.search === '' ? ("hand") : null} onClick={this.sort('nom')}>
                  <b><Translate contentKey="pimnowApp.product.nom"/></b>&nbsp;
                  {this.state.search === '' ? (<FontAwesomeIcon icon="sort"/>) : null}
                </th>
                <th className={this.state.search === '' ? ("hand") : null} onClick={this.sort('description')}>
                  <b><Translate contentKey="pimnowApp.product.description"/></b>&nbsp;
                  {this.state.search === '' ? (<FontAwesomeIcon icon="sort"/>) : null}
                </th>
                <th className={this.state.search === '' ? ("hand") : null} onClick={this.sort('family.nom')}>
                  <b><Translate contentKey="pimnowApp.product.family"/></b>&nbsp;
                  {this.state.search === '' ? (<FontAwesomeIcon icon="sort"/>) : null}
                </th>
                <th className={this.state.search === '' ? ("hand") : null} onClick={this.sort('categories.nom')}>
                  <b><Translate contentKey="pimnowApp.product.categories"/></b>&nbsp;
                  {this.state.search === '' ? (<FontAwesomeIcon icon="sort"/>) : null}
                </th>
                <th/>
              </tr>
              </thead>
              <tbody>
              {productList.map((product, i) => (
                !this.state.loading ? (
                  <tr key={`entity-${i}`}>
                    <td style={{verticalAlign: 'middle'}}>
                      <Checkbox
                        key={Math.random()}
                        id={product.idF}
                        onChange={this.handleChange(product.id)}
                        checked={this.state.checkList.get(product.id)}
                      />
                    </td>
                    <td style={{verticalAlign: 'middle'}}>{product.idF}</td>
                    <td style={{verticalAlign: 'middle'}}>{product.nom}</td>
                    <td style={{verticalAlign: 'middle'}}>{product.description}</td>
                    <td style={{verticalAlign: 'middle'}}>{product.family ? <Link
                      to={`family`}>({product.family.idF}) {product.family.nom}</Link> : ''}</td>
                    <td
                      style={{verticalAlign: 'middle'}}>{product.categories ? this.stringCategories(product) : ''}</td>
                    <td className="text-right" style={{verticalAlign: 'middle'}}>
                      <div className="btn-group flex-btn-group-container">
                        <Tooltip title={<Translate contentKey="global.tooltip.see"/>} arrow>
                          <Link to={`${match.url}/${product.id}`}>
                            <IconButton aria-label="watch">
                              <VisibilityIcon style={{fill: "#245173"}}/>
                            </IconButton>
                          </Link>
                        </Tooltip>
                        <Tooltip title={<Translate contentKey="global.tooltip.edit"/>} arrow>
                          <Link to={`${match.url}/${product.id}/edit`}>
                            <IconButton aria-label="edit">
                              <EditIcon style={{fill: "#c8994b"}}/>
                            </IconButton>
                          </Link>
                        </Tooltip>
                        {this.state.isUser === false ? (
                          <Tooltip title={<Translate contentKey="global.tooltip.delete"/>} arrow>
                          <Link to={`${match.url}/${product.id}/delete`}>
                            <IconButton aria-label="delete">
                              <DeleteIcon style={{fill: "#d45e37"}}/>
                            </IconButton>
                          </Link>
                        </Tooltip>) : null}
                      </div>
                    </td>
                  </tr>
                ) : this.skeletonLines()
              ))}
              </tbody>
            </Table>
          ) : (
            <div className="alert alert-warning">
              <Translate contentKey="pimnowApp.product.home.notFound">No Products found</Translate>
            </div>
          )}
        </div>
        <div className={productList && productList.length > 0 ? '' : 'd-none'}>
          <Row className="justify-content-center">
            <JhiItemCount page={this.state.activePage} total={totalItems} itemsPerPage={this.state.itemsPerPage}
                          i18nEnabled/>
          </Row>
          <Row className="justify-content-center">
            <JhiPagination
              activePage={this.state.activePage}
              onSelect={this.handlePagination}
              maxButtons={5}
              itemsPerPage={this.state.itemsPerPage}
              totalItems={this.props.totalItems}
            />
          </Row>
        </div>
      </div>
    );
  }
}

const mapStateToProps = (storeState) => ({
  productList: storeState.product.entities,
  totalItems: storeState.product.totalItems,
  loadingIntegration: storeState.product.loadingIntegration,
  loadingIntegrationRemove: storeState.product.loadingIntegrationRemove,
  selectAllList: storeState.product.selectAllList,
  loading: storeState.product.loading,
  selectAllListWithSearch: storeState.product.selectAllListWithSearch,
  account: storeState.authentication.account
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
  integrateToPrestashop,
  removeIntegrationFromPrestashop,
  selectAll,
  getSearchEntitiesForSelectAll
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Product);
