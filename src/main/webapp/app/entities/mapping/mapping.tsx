import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Row, Table} from 'reactstrap';
import {getSortState, IPaginationBaseState, JhiItemCount, JhiPagination, Translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './mapping.reducer';
import {ITEMS_PER_PAGE} from 'app/shared/util/pagination.constants';
import {IconButton, Tooltip} from "@material-ui/core";
import VisibilityIcon from "@material-ui/icons/Visibility";
import ControlPointDuplicateIcon from '@material-ui/icons/ControlPointDuplicate';
import EditIcon from '@material-ui/icons/Edit';
import {AddButton} from "app/commons/add-button";
import {AUTHORITIES} from "app/config/constants";


export interface IMappingProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IMappingState extends IPaginationBaseState {
  search: string;
  isUser: boolean;
}

export class Mapping extends React.Component<IMappingProps, IMappingState> {
  state: IMappingState = {
    isUser: false,
    search: '',
    ...getSortState(this.props.location, ITEMS_PER_PAGE)
  };

  componentDidMount() {
    this.getEntities();
    if(this.props.account.authorities[0] === AUTHORITIES.USER){
      this.setState({isUser: true});
    }
  }


  sort = prop => () => {
    this.setState(
      {
        order: this.state.order === 'asc' ? 'desc' : 'asc',
        sort: prop
      },
      () => this.sortEntities()
    );
  };

  sortEntities() {
    this.getEntities();
    this.props.history.push(`${this.props.location.pathname}?page=${this.state.activePage}&sort=${this.state.sort},${this.state.order}`);
  }

  handlePagination = activePage => this.setState({ activePage }, () => this.sortEntities());

  getEntities = () => {
    const { activePage, itemsPerPage, sort, order, search } = this.state;
    if (search) {
      this.props.getSearchEntities(search, activePage - 1, itemsPerPage, `${sort},${order}`);
    } else {
      this.props.getEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
    }
  };

  render() {
    const { mappingList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="mapping-heading">
          <Translate contentKey="pimnowApp.mapping.home.title">Mappings</Translate>
          {this.state.isUser === false ? (
            <Link to={`${match.url}/new`} className="float-right">
            <AddButton content={"pimnowApp.mapping.home.createLabel"}/>
          </Link>) : null}
        </h2>
        <br/>
        <div className="table-responsive">
          {mappingList && mappingList.length > 0 ? (
            <Table responsive aria-describedby="mapping-heading">
              <thead>
                <tr>
                  <th className="hand" onClick={this.sort('idF')}>
                    <Translate contentKey="pimnowApp.mapping.idF">Id F</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('name')}>
                    <Translate contentKey="pimnowApp.mapping.name">Name</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('description')}>
                    <Translate contentKey="pimnowApp.mapping.description">Description</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('separator')}>
                    <Translate contentKey="pimnowApp.mapping.separator">Separator</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {mappingList.map((mapping, i) => (
                  <tr key={`entity-${i}`}>
                    <td>{mapping.idF}</td>
                    <td>{mapping.name}</td>
                    <td>{mapping.description}</td>
                    <td>{mapping.separator}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Tooltip title={<Translate contentKey="global.tooltip.see"/>}>
                        <Link to={`${match.url}/info/${mapping.id}`}>
                          <IconButton aria-label="watch">
                            <VisibilityIcon style={{fill: "#245173"}}/>
                          </IconButton>
                        </Link>
                        </Tooltip>
                        {this.state.isUser === false ? (
                          <React.Fragment>
                            <Tooltip title={<Translate contentKey="global.tooltip.duplicate"/>}>
                            <Link to={`${match.url}/duplicate/${mapping.id}`}>
                            <IconButton aria-label="watch">
                              <ControlPointDuplicateIcon style={{fill: "#245173"}}/>
                            </IconButton>
                          </Link>
                            </Tooltip>
                            <Tooltip title={<Translate contentKey="global.tooltip.edit"/>}>
                          <Link to={`${match.url}/edit/${mapping.id}`}>
                          <IconButton aria-label="watch">
                          <EditIcon style={{fill: "rgb(200, 153, 75)"}}/>
                          </IconButton>
                          </Link>
                          </Tooltip>
                          </React.Fragment>) : null}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <div className="alert alert-warning">
              <Translate contentKey="pimnowApp.mapping.home.notFound">No Mappings found</Translate>
            </div>
          )}
        </div>
        <div className={mappingList && mappingList.length > 0 ? '' : 'd-none'}>
          <Row className="justify-content-center">
            <JhiItemCount page={this.state.activePage} total={totalItems} itemsPerPage={this.state.itemsPerPage} i18nEnabled />
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
  mappingList: storeState.mapping.entities,
  totalItems: storeState.mapping.totalItems,
  account: storeState.authentication.account
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Mapping);
