function myget(object, property) {
  return object[property];
}

function myhas(object, property) {
  return object.hasOwnProperty(property);
}

const MyGet = { myget, myhas };
export default MyGet;
