let TEMP_API = {
  '401': {
    url: 'https://run.mocky.io/v3/7a98985c-1e59-4bfb-87dd-117307b6196c',
    args: {}
  },
  '200': {
    url: 'https://jsonplaceholder.typicode.com/todos/2',
    args: {}
  },
  '404': {
    url: 'https://jsonplaceholder.typicode.com/todos/1',
    args: {
      method: "POST",
      credentials: "include"
    }
  }
}

const originalFetch = fetch;
fetch = function() {
  let self = this;
  let args = arguments;
  return originalFetch.apply(self, args).then(async function(data) {
    if (data.status === 200) console.log("---------Status 200----------");
    if (data.status === 401) {
      // request for token with original fetch if status is 401
      console.log('failed');
      let response = await originalFetch(TEMP_API['200'].url, TEMP_API['200'].args);
      // if status is 401 from token api return empty response to close recursion
      console.log("==========401 UnAuthorize.=============");
      console.log(response);
      if (response.status === 401) {
        return {};
      }
      // else set token
      // recall old fetch
      // here i used 200 because 401 or 404 old response will cause it to rerun
      // return fetch(...args); <- change to this for real scenarios
      // return fetch(args[0], args[1]); <- or to this for real sceaerios
      return fetch(TEMP_API['200'].url, TEMP_API['200'].args);
    }
    // condition will be tested again after 401 condition and will be ran with old args
    if (data.status === 404) {
      console.log("==========404 Not Found.=============");
      // here i used 200 because 401 or 404 old response will cause it to rerun
      // return fetch(...args); <- change to this for real scenarios
      // return fetch(args[0], args[1]); <- or to this for real scenarios
      return fetch(TEMP_API['200'].url, TEMP_API['200'].args);
sceaerios
    } else {
      return data;
    }
  });
};

(async function() {
  console.log("==========Example1=============");
  let example1 = await fetch(TEMP_API['404'].url, TEMP_API['404'].args);
  console.log(example1);
  console.log("==========Example2=============");
  let example2 = await fetch(TEMP_API['200'].url, TEMP_API['200'].args);
  console.log(example2);
  console.log("==========Example3=============");
  let example3 = await fetch(TEMP_API['401'].url, TEMP_API['401'].args);
  console.log(example3);
})();