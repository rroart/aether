import React from 'react';

import ConfigTree from './ConfigTree';

function Configuration( { props, config, configname } ) {
  if (config === undefined ||config == null || config == "") {
    return(
      <div>
        <p>Empty</p>
      </div>);
  }
  return(
    <div>
      <p>Not empty</p>
      <ConfigTree props = { props } config =  { config } configname = { configname } />
    </div>);
}

export default Configuration;
