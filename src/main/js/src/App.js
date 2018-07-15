import React from 'react';
import MapGL, {Marker, Popup, NavigationControl} from 'react-map-gl';
import './App.css';

import SatellitePin from './SatellitePin';
import SatelliteInfo from './SatelliteInfo';

const client = require('./client');
const stompClient = require('./websocket-listener');

const TOKEN = process.env.REACT_APP_MAPBOX_TOKEN;

const navStyle = {
	position: 'absolute',
	top: 0,
	left: 0,
	padding: '10px'
};

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			satellites: [],
			viewport: {
				latitude: 0.0,
				longitude: 0.0,
				zoom: 0.0,
				bearing: 0,
				pitch: 0,
				width: 500,
				height: 500,
			},
			popupInfo: null,
		};
        this.loadAndRefresh = this.loadAndRefresh.bind(this);
        this.updateParameters = this.updateParameters.bind(this);
		// 	this.width = 500;
		// 	this.height = 500;
	}

    loadAndRefresh() {
        client({method: 'GET', path: '/api/satellites'}).done(response => {
			this.setState({satellites: response.entity._embedded.satellites});
		});
    }

    updateParameters(message) {
		this.loadAndRefresh()
	}

	_resize = () => {
		this.setState({
			viewport: {
				...this.state.viewport,
				width: this.mapWidth || Math.round(0.8 * window.innerWidth),
				height: this.mapHeight || window.innerHeight
			}
		});
	};

	_updateViewport = (viewport) => {
		this.setState({viewport});
	}

	_renderSatelliteMarker = (satellite, index) => {
		return (
			<Marker key={`marker-${index}`}
				longitude={satellite.lon}
				latitude={satellite.lat}>
				<SatellitePin size={20}
					color={satellite.color}
					onClick={() => this.setState({popupInfo: true})} />
			</Marker>
		);
    }

	_renderPopup = (satellite, index) => {
		const {popupInfo} = this.state;
		return popupInfo && (
			<Popup key={`marker-${index}`}
				tipSize={5}
				anchor="top"
				longitude={satellite.lon}
				latitude={satellite.lat}
				onClose={() => this.setState({popupInfo: null})} >
				<SatelliteInfo sat={satellite} />
			</Popup>
		);
    }

	componentDidMount() {
		this.loadAndRefresh()

        stompClient.register([
			{route: '/update/newParameters', callback: this.updateParameters}
		]);

		window.addEventListener('resize', this._resize);
	    this._resize();
	}

	componentWillUnmount() {
      	window.removeEventListener('resize', this._resize);
    }

	render() {
		const {viewport} = this.state;

		return (
			<div className="App">
				<SatelliteList satellites={this.state.satellites}/>
				<MapGL
					{...viewport}
					mapStyle="mapbox://styles/mapbox/dark-v9"
					onViewportChange={this._updateViewport}
					mapboxApiAccessToken={TOKEN}>

					{ this.state.satellites.map(this._renderSatelliteMarker) }

					{ this.state.satellites.map(this._renderPopup) }

					<div className="nav" style={navStyle}>
						<NavigationControl
							onViewportChange={this._updateViewport}/>
					</div>
				</MapGL>
			</div>
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
						<th>Satellite</th>
						<th>a</th>
						<th>e</th>
						<th>i</th>
                        <th>pa</th>
						<th>raan</th>
						<th>omega</th>
						<th>lon</th>
						<th>lat</th>
					</tr>
					{satellites}
				</tbody>
			</table>
		)
	}
}

class Satellite extends React.Component{
	render() {
		var satNum = this.props.satellite._links.self.href;
		satNum = satNum.split('/');
		satNum = satNum[satNum.length - 1];

		return (
			<tr>
				<td bgcolor={this.props.satellite.color}>QBEE {satNum}</td>
				<td>{this.props.satellite.a.toFixed(4)}</td>
				<td>{this.props.satellite.e.toFixed(4)}</td>
				<td>{this.props.satellite.i.toFixed(4)}</td>
                <td>{this.props.satellite.omega.toFixed(4)}</td>
				<td>{this.props.satellite.raan.toFixed(4)}</td>
				<td>{this.props.satellite.lm.toFixed(4)}</td>
				<td>{this.props.satellite.lon.toFixed(4)}</td>
				<td>{this.props.satellite.lat.toFixed(4)}</td>
			</tr>
		)
	}
}

export default App;
