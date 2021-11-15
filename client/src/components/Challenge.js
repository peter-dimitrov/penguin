import axios from "axios";
import React, { useState, useEffect } from 'react';

export default function Challenge() {

  const [cost, setCost] = useState(0);
  const [inventory, setInventory] = useState({})
  const [lowStock, setLowStock] = useState([])
  const [lowStockOnly, setLowStockOnly] = useState(false)
  const [state, setState] = useState([]);
  
  useEffect(() => {
    axios.get("http://localhost:4567/inventory").then((response) => setInventory(response.data))
  }, [])


  function handleChange(e) {
    e.preventDefault();
    setState({ [e.target.title]: state[e.target.title] });
    let inv = {...inventory}
    if(inv[e.target.title][0] + parseInt(state[e.target.title]) <= inv[e.target.title][1] && parseInt(state[e.target.title]) >= 0){
      inv[e.target.title][0] += parseInt(state[e.target.title])
      setInventory(inv)
    }
      let temp = {...state}
      delete temp[e.target.title]
      setState(temp)
  }

  function handleRestock(e){
    setState({ [e.target.title]: e.target.value})
  };

  const ItemRow = (props) => {
    return(
      <tr>
        <td>{props.sku}</td>
        <td>{props.item}</td>
        <td>{props.stock}</td>
        <td>{props.capacity}</td>
        <td>
        <form title = {props.item} onSubmit = {(e) => handleChange(e)}>
        <input type = "number" title = {props.item} value={state[props.item]} onChange = {handleRestock}/>
        </form>
        </td>
      </tr>
    )
  }

  const items = Object.keys(inventory).map((item, index) => <ItemRow key={index} item = {item} stock = {inventory[item][0]} capacity = {inventory[item][1]} sku = {inventory[item][2]}/>)
  
  const lowStockItems = lowStock.map((item, index) => <ItemRow key={index} item = {item} stock = {inventory[item][0]} capacity = {inventory[item][1]} sku = {inventory[item][2]}/>)


  return (
    <>
      <table>
        <thead>
          <tr>
            <td>SKU</td>
            <td>Item Name</td>
            <td>Amount in Stock</td>
            <td>Capacity</td>
            <td>Order Amount (value cannot be greater than capacity - current stock)</td>
          </tr>
        </thead>
        <tbody>

        {!lowStockOnly && items}
        {lowStockOnly && lowStockItems}

          {/* TODO: Create an <ItemRow /> component that's rendered for every inventory item. The component
          will need an input element in the Order Amount column that will take in the order amount and 
          update the application state appropriately. */}

          
        </tbody>
      </table>

      {/* TODO: Display total cost returned from the server */}
      <div>Total Cost: {cost}</div>
      {/* 
      TODO: Add event handlers to these buttons that use the Java API to perform their relative actions.
      */}
      {!lowStockOnly && 
      <button onClick = {(e) => axios.get("http://localhost:4567/low-stock")
        .then((response) => setLowStock(response.data), setLowStockOnly(!lowStockOnly))}>
        Get Low-Stock Items
      </button>}

      {lowStockOnly && 
      <button onClick = {(e) => setLowStockOnly(!lowStockOnly)}>
      See All Items
      </button>}

      <button onClick = {(e) => axios.post("http://localhost:4567/restock-cost")
        .then((response) => setCost(response.data))}>
      Determine Re-Order Cost (for Low-Stock Items)</button>

    </>
  );
}
