import React from 'react';

import { MyMap } from '../util'

function TreeView( { props, config, map, configname } ) {
  function getinput(type, key, value) {
    if (type == "java.lang.Boolean") {
      return (<input type="checkbox" defaultChecked={value} onChange={e => handleCheckChange(e, key, props)}/>);
    }
    if (value != null) {
      return (<input type="text" onChange={e => handleChange(e, key, props)} defaultValue={value}/>);
    }
  }

  function handleCheckChange(event, key, props) {
    props.setconfigvaluemap([ configname, key, event.target.checked ]);
  }

  function handleChange(event, key, props) {
    props.setconfigvaluemap([ configname, key, event.target.value ]);
  }

  function getview(value, key, date) {
    const mykey = date + key;
    return(
      <li key={mykey}>
        <TreeView props = {props} config = {config} map={value} configname = { configname } />
      </li>
    )
  }

  const textMap = MyMap.myget(config, 'text');
  const typeMap = MyMap.myget(config, 'type');
  const valueMap = MyMap.myget(config, 'configValueMap');
  const name =  MyMap.myget(map, 'name');
  const text =  MyMap.myget(textMap, name);
  const value =  MyMap.myget(valueMap, name);
  const type =  MyMap.myget(typeMap, name);
  const myinput = getinput(type, name, value);
  const confMap =  MyMap.myget(map, 'configTreeMap');
  if (confMap === undefined) {
    console.log("ccccc" + Object.keys(map));
    console.log("ccccc" + (map));
    return;
  }
  const now = Date.now();
  const map2 = MyMap.mymap(confMap);
  const itemlist = [];
  for (let [key, value] of map2) {
    itemlist.push(getview(value, key, now));
  }
  const map3 = itemlist; // Array.from(itemlist);
  return(
    <div>{myinput}{text}({name})
      <ul>
        { map3 }
      </ul>
    </div>
  )
}

export default TreeView;
