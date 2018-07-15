import React, {PureComponent} from 'react';

export default class SatelliteInfo extends PureComponent {

    render() {
        var satNum = this.props.sat._links.self.href.split('/');
        satNum = satNum[satNum.length - 1];
        return (
            <div>
                QBEE {satNum}
            </div>
        );
    }

}
