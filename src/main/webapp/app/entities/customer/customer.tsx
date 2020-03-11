import React from 'react';
import {connect} from 'react-redux';
import {Link, RouteComponentProps} from 'react-router-dom';
import {Col, InputGroup, Row, Table} from 'reactstrap';
import {getSortState, IPaginationBaseState, JhiItemCount, JhiPagination, Translate, translate} from 'react-jhipster';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';

import {IRootState} from 'app/shared/reducers';
import {getEntities, getSearchEntities} from './customer.reducer';
import {ITEMS_PER_PAGE} from 'app/shared/util/pagination.constants';
import {Button, IconButton, Tooltip} from "@material-ui/core";
import VisibilityIcon from '@material-ui/icons/Visibility';
import EditIcon from '@material-ui/icons/Edit';
import DeleteIcon from '@material-ui/icons/Delete';
import AddBoxIcon from '@material-ui/icons/AddBox';
import BusinessCenter from "@material-ui/icons/BusinessCenter";
import {AddButton} from "app/commons/add-button";


export interface ICustomerProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface ICustomerState extends IPaginationBaseState {
  search: string;
}

export class Customer extends React.Component<ICustomerProps, ICustomerState> {
  state: ICustomerState = {
    search: '',
    ...getSortState(this.props.location, ITEMS_PER_PAGE)
  };

  componentDidMount() {
    this.getEntities();
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
    const { customerList, match, totalItems } = this.props;
    return (
      <div>
        <h2 id="customer-heading">
          <BusinessCenter style={{fill: "#245173", marginBottom: '0.3em'}} fontSize="large"/>
          &nbsp;
          <Translate contentKey="pimnowApp.customer.home.title"/>
          <Link to={`${match.url}/new`} className="float-right" >
            <AddButton content={"pimnowApp.customer.home.createLabel"}/>
          </Link>
        </h2>
        <br/>
        <div className="table-responsive">
          {customerList && customerList.length > 0 ? (
            <Table responsive aria-describedby="customer-heading">
              <thead>
              <tr>
                <th className="hand" onClick={this.sort('name')}>
                  <Translate contentKey="pimnowApp.customer.name">Name</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={this.sort('description')}>
                  <Translate contentKey="pimnowApp.customer.description">Description</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                </th>
              </tr>
              </thead>
              <tbody>
              {customerList.map((customer, i) => (
                <tr key={`entity-${i}`}>
                  <td>{customer.name}</td>
                  <td>{customer.description}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Tooltip title={<Translate contentKey="global.tooltip.see"/>} arrow>
                        <Link to={`${match.url}/${customer.id}/detail`}>
                        <IconButton aria-label="watch">
                          <VisibilityIcon style={{fill: "#245173"}}/>
                        </IconButton>
                      </Link>
                      </Tooltip>
                      <Tooltip title={<Translate contentKey="global.tooltip.edit"/>} arrow>
                      <Link to={`${match.url}/${customer.id}/edit`}>
                      <IconButton aria-label="edit">
                        <EditIcon style={{fill: "#c8994b"}}/>
                      </IconButton>
                      </Link>
                      </Tooltip>
                      <Tooltip title={<Translate contentKey="global.tooltip.delete"/>} arrow>
                      <Link to={`${match.url}/${customer.id}/delete`}>
                        <IconButton aria-label="delete">
                          <DeleteIcon style={{fill: "#d45e37"}} />
                        </IconButton>
                      </Link>
                      </Tooltip>
                    </div>
                  </td>
                </tr>
              ))}
              </tbody>
            </Table>
          ) : (
            <div className="alert alert-warning">
              <Translate contentKey="pimnowApp.customer.home.notFound">No Customers found</Translate>
            </div>
          )}
        </div>
        <div className={customerList && customerList.length > 0 ? '' : 'd-none'}>
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

const mapStateToProps = ({ customer }: IRootState) => ({
  customerList: customer.entities,
  totalItems: customer.totalItems
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
)(Customer);
