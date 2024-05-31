import { Route, Routes } from "react-router-dom"

import SpreadsheetLayout from './shared/SpreadsheetLayout';

import Input from "./input/Input";
import Table from "./table/Table";

function Spreadsheet() {
  return (
    <Routes>
      <Route path='/' element={<SpreadsheetLayout />}>
        <Route index element={<Input />} />
        <Route path='table' element={<Table />} />
      </Route>
    </Routes>
  )
}

export default Spreadsheet