import {useState, useEffect} from "react";
import logo from './logo.svg';
import './App.css';
import {CONFIG} from "./index.js";

function App() {
  
  const [addItemKey, setAddItemKey] = useState("");
  const [addItemPersonName, setAddItemPersonName] = useState("");
  const [addItemResponse, setAddItemResponse] = useState({});
  
  const [getItemKey, setGetItemKey] = useState("");
  const [getItemResponse, setGetItemResponse] = useState({});
  
  
  async function handleAddData() {
    try {
      setAddItemResponse("Loading ...");
      
      const data = {
        "key": addItemKey,
        [CONFIG.DYNAMODB_PERSON_NAME]: addItemPersonName
      }
      
      const response = await fetch(CONFIG.ADD_ENDPOINT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      const responseJson = await response.json();
      setAddItemResponse(responseJson);
    } catch (e) {
      setAddItemResponse("Error ...");
    }
  }
  
  async function handleGetData() {
    try {
      setGetItemResponse("Loading ...");
      const response = await fetch(`${CONFIG.GET_ENDPOINT}/${getItemKey}`);
      const responseJson = await response.json();
      setGetItemResponse(responseJson);
    } catch (e) {
      setGetItemResponse("Error ...");
    }
  }
  
  
  return (
    <div className="App" style={{backgroundColor: "#282c34", height: "200vh"}}>
      <img src={logo} className="App-logo" alt="logo" />
      
      <div style={{ backgroundColor: "teal", width: "30%", margin: "0 auto", marginTop: "5rem", padding: "1rem 2rem", borderRadius: "20px" }}>
        <input type="text" placeholder="key" onChange={e => setAddItemKey(e.target.value)} />
        <input type="text" placeholder="personName" onChange={e => setAddItemPersonName(e.target.value)} />
        <button onClick={handleAddData}>Add Item</button>
        <p style={{ fontSize: "1.1rem", marginBottom: "0" }}><b>/add-item API Response:</b><br /> {JSON.stringify(addItemResponse)}</p>
      </div>


      <br />
      <br />
      <div style={{ backgroundColor: "teal", width: "30%", margin: "0 auto", padding: "1rem 2rem", borderRadius: "20px" }}>
        <input type="text" placeholder="key" onChange={e => setGetItemKey(e.target.value)} />
        <button onClick={handleGetData}>Get Item</button>
        <p style={{ fontSize: "1.1rem", marginBottom: "0" }}><b>/get-item API response:</b><br /> {JSON.stringify(getItemResponse)}</p>
      </div>
    </div>
  );
}

export default App;
