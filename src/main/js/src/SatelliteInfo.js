import React, {PureComponent} from 'react';

export default class SatelliteInfo extends PureComponent {

    render() {
        var satNum = this.props.sat._links.self.href.split('/');
        satNum = satNum[satNum.length - 1];
        return (
            <div style={{margin: "10px 5px 0px 5px"}}>
                QBEE {satNum}
            </div>
        );
    }

}
