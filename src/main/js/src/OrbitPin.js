import React, {PureComponent} from 'react';

const pinStyle = {
  cursor: 'pointer',
  stroke: 'none'
};

export default class SatellitePin extends PureComponent {

  render() {
    const {size = 5, color, onClick} = this.props;

    return (
      <svg height={size} viewBox='0 0 24 24'
        style={{...pinStyle,
            fill: color,
            transform: `translate(${-size/2}px,${-size}px)`}}
        onClick={onClick} >
        <circle cx={size} cy={size} r={size} />
      </svg>
    );
  }
}
