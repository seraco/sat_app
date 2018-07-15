import React from 'react';
import './App.css';

const client = require('./client');
const stompClient = require('./websocket-listener');

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {satellites: []};
        this.loadAndRefresh = this.loadAndRefresh.bind(this);
        this.updateParameters = this.updateParameters.bind(this);
	}

    loadAndRefresh() {
        client({method: 'GET', path: '/api/satellites'}).done(response => {
			this.setState({satellites: response.entity._embedded.satellites});
		});
    }

    updateParameters(message) {
		this.loadAndRefresh()
	}

	componentDidMount() {
        stompClient.register([
			{route: '/update/newParameters', callback: this.updateParameters}
		]);
	}

	render() {
		return (
			<SatelliteList satellites={this.state.satellites}/>
		)
	}
}

class SatelliteList extends React.Component{
	render() {
		var satellites = this.props.satellites.map(satellite =>
			<Satellite key={satellite._links.self.href} satellite={satellite}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>a</th>
						<th>e</th>
						<th>i</th>
                        <th>pa</th>
						<th>raan</th>
						<th>omega</th>
					</tr>
					{satellites}
				</tbody>
			</table>
		)
	}
}

class Satellite extends React.Component{
	render() {
		return (
			<tr>
				<td>{this.props.satellite.a}</td>
				<td>{this.props.satellite.e}</td>
				<td>{this.props.satellite.i}</td>
                <td>{this.props.satellite.omega}</td>
				<td>{this.props.satellite.raan}</td>
				<td>{this.props.satellite.lm}</td>
			</tr>
		)
	}
}

export default App;
