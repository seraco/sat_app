import React from 'react';
import StaticMap, {Marker, Popup} from 'react-map-gl';

import './App.css';

import SatellitePin from './SatellitePin';
import OrbitPin from './OrbitPin';
import SatelliteInfo from './SatelliteInfo';

const client = require('./client');
const stompClient = require('./websocket-listener');

const TOKEN = process.env.REACT_APP_MAPBOX_TOKEN;

class App extends React.Component {

	polyline;

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
			orbitMarkers: [],
			hideOrbitMark: false,
		};
        this.loadAndRefresh = this.loadAndRefresh.bind(this);
        this.updateParameters = this.updateParameters.bind(this);
		this.mapWidth = 770;
		this.mapHeight = 550;
	}

    loadAndRefresh() {
        client({method: 'GET', path: '/api/satellites'}).done(response => {
			this.setState({satellites: response.entity._embedded.satellites});
		});
    }

    updateParameters(message) {
		this.loadAndRefresh();

		var newState = [];

		for (let i = 0; i < this.state.satellites.length; i++) {
			newState.push({
				latitude: this.state.satellites[i].lat,
				longitude: this.state.satellites[i].lon,
				color: this.state.satellites[i].color,
			});
		}

		this.setState(prevState => ({
			orbitMarkers: prevState.orbitMarkers.length > 5000 ?
				[] :
				prevState.orbitMarkers.concat(newState)
		}))
	}

	_resize = () => {
		this.setState({
			viewport: {
				...this.state.viewport,
				width: this.mapWidth || Math.round(0.7 * window.innerWidth),
				height: this.mapHeight || Math.round(0.5 * window.innerWidth)
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

	_renderOrbitMarker = (coords, index) => {
		return (
			<Marker key={`marker-${index}`}
				longitude={coords.longitude}
				latitude={coords.latitude}>
				<OrbitPin size={5}
					color={coords.color}
					onClick={() => this.setState({popupInfo: true})} />
			</Marker>
		);
    }

	_hideOrbits = () => {
		this.setState(prevState => ({
			hideOrbitMark: !prevState.hideOrbitMark
		}));
	}

	componentDidMount() {
		// this.loadAndRefresh()

        stompClient.register([
			{route: '/update/newParameters', callback: this.updateParameters}
		]);

		window.addEventListener('resize', this._resize);
	    this._resize();

		this.reactMap.getMap().on('load', () => {
			this.reactMap.getMap().addLayer({
		        "id": "route",
		        "type": "line",
		        "source": {
		            "type": "geojson",
		            "data": {
		                "type": "Feature",
		                "properties": {},
		                "geometry": {
		                    "type": "LineString",
		                    "coordinates": [
		                        [-122.48369693756104, 37.83381888486939],
		                        [-122.48348236083984, 37.83317489144141],
		                        [-122.48339653015138, 37.83270036637107],
		                        [-122.48356819152832, 37.832056363179625],
		                        [-122.48404026031496, 37.83114119107971],
		                        [-122.48404026031496, 37.83049717427869],
		                        [-122.48348236083984, 37.829920943955045],
		                        [-122.48356819152832, 37.82954808664175],
		                        [-122.48507022857666, 37.82944639795659],
		                        [-122.48610019683838, 37.82880236636284],
		                        [-122.48695850372314, 37.82931081282506],
		                        [-122.48700141906738, 37.83080223556934],
		                        [-122.48751640319824, 37.83168351665737],
		                        [-122.48803138732912, 37.832158048267786],
		                        [-122.48888969421387, 37.83297152392784],
		                        [-122.48987674713133, 37.83263257682617],
		                        [-122.49043464660643, 37.832937629287755],
		                        [-122.49125003814696, 37.832429207817725],
		                        [-122.49163627624512, 37.832564787218985],
		                        [-122.49223709106445, 37.83337825839438],
		                        [-122.49378204345702, 37.83368330777276]
		                    ]
		                }
		            }
		        },
		        "layout": {
		            "line-join": "round",
		            "line-cap": "round"
		        },
		        "paint": {
		            "line-color": "red",
		            "line-width": 8
		        }
		    });
		})
	}

	componentWillUnmount() {
      	window.removeEventListener('resize', this._resize);
    }

	render() {
		const {viewport} = this.state;

		return (
			<div className="App">
				<button onClick={this._hideOrbits}>
					Toggle Ground Tracks
				</button>
				<SatelliteList satellites={this.state.satellites}/>
				<div className="MapContainer"><div className="Map">
					<StaticMap
						ref={(reactMap) => {this.reactMap = reactMap;}}
						{...viewport}
						mapStyle="mapbox://styles/mapbox/dark-v9"
						onViewportChange={this._updateViewport}
						mapboxApiAccessToken={TOKEN}>

						{this.state.satellites.map(this._renderSatelliteMarker)}

						{this.state.hideOrbitMark
						&& this.state.orbitMarkers.map(this._renderOrbitMarker)}

						{this.state.satellites.map(this._renderPopup)}
					</StaticMap>
				</div></div>
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
			<div className="SatelliteList">
				<table>
					<tbody>
						<tr>
							<th>Satellite</th>
							<th>a [km]</th>
							<th>e [ ]</th>
							<th>i [rad]</th>
	                        <th>pa [rad]</th>
							<th>raan [rad]</th>
							<th>omega [rad]</th>
							<th>lon [deg]</th>
							<th>lat [deg]</th>
						</tr>
						{satellites}
					</tbody>
				</table>
			</div>
		)
	}
}

class Satellite extends React.Component{
	render() {
		var satNum = this.props.satellite._links.self.href;
		satNum = satNum.split('/');
		satNum = satNum[satNum.length - 1];
		var aInKm = this.props.satellite.a / 1000.0;

		return (
			<tr>
				<td bgcolor={this.props.satellite.color}>QBEE {satNum}</td>
				<td>{aInKm.toFixed(4)}</td>
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
