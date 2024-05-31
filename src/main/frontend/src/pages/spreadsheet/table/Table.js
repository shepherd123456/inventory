import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom"
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash, faUpDown } from '@fortawesome/free-solid-svg-icons';
import { useCtx } from "../../../hooks/useCtx";
import {
  flexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useReactTable
} from '@tanstack/react-table';

function Table() {
  const { ctx } = useCtx();

  const navigate = useNavigate();
  const location = useLocation();
  const state = location.state;

  const [spid, setSpid] = useState('');
  const [workName, setWorkName] = useState('');
  const [columns, setColumns] = useState([]);
  const [data, setData] = useState([]);
  const [globalFilter, setGlobalFilter] = useState('');
  const [pagination, setPagination] = useState({
    pageIndex: 0,
    pageSize: 14
  });
  const [showNewColumnForm, setShowNewColumnForm] = useState(false);
  const [newColumn, setNewColumn] = useState('');
  const [showDeleteColumnDialog, setShowDeleteColumnDialog] = useState(false);
  const [columnToDelete, setColumnToDelete] = useState('');

  useEffect(() => {
    async function loadData() {
      if (state == null) {
        navigate('/spreadsheet');
      } else {
        const sp = state.spid;
        const wn = state.workName;
        setSpid(sp);
        setWorkName(wn);
        const res = await ctx.axios.get(`/construction-work/${sp}/${wn}`);
        initTable(res.data);
      }
    }
    loadData();
  }, []);

  function initTable(table) {
    setColumns(table.headers.map(h => ({
      accessorKey: h,
      header: h,
      cell: EditableCell
    })));
    setData(table.data);
  }

  async function onSave() {
    await ctx.axios.put(`/construction-work/${spid}/${workName}`, {
      headers: columns.map(h => h.accessorKey),
      data
    });
  }

  async function download() {
    const res = await ctx.axios.get(`/spreadsheet/download/${spid}`, {
      responseType: 'blob'
    });
    const disposition = res.headers['content-disposition'];
    const filename = disposition ? disposition.split('filename=')[1] : 'atributy.xlsx';
    const url = URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }

  const table = useReactTable({
    data,
    columns,
    state: {
      columnOrder: ["Stavební_díl-ID", "Allright_Stavebni_dil_ID"],
      globalFilter,
      pagination
    },
    getCoreRowModel: getCoreRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getSortedRowModel: getSortedRowModel(),
    columnResizeMode: 'onChange',
    onPaginationChange: setPagination,
    meta: {
      updateData: (rowIndex, columnId, value) => setData(
        prev => prev.map((row, index) => index === rowIndex ? {
          ...prev[index],
          [columnId]: value
        } : row)
      ),
      addRow: () => {
        const newData = [...data];
        const emptyRow = {};
        columns.forEach(col => {
          emptyRow[col.accessorKey] = '';
        });
        newData.splice((pagination.pageIndex + 1) * pagination.pageSize - 1, 0, emptyRow); // Insert at the end of current page
        setData(newData);
      },
      addColumn: () => {
        if (!columns.find(col => col.accessorKey === newColumn)) {
          const c = {
            accessorKey: newColumn,
            header: newColumn,
            cell: EditableCell
          };
          setColumns(prev => [...prev, c]);
        }
      },
      removeColumn: () => {
        const filteredColumns = columns.filter(c => c.accessorKey != columnToDelete);
        setColumns(filteredColumns);
        const newData = [...data];
        for (let i = 0; i < newData.length; i++) {
          delete newData[i][columnToDelete];
        }
        setData(newData);
      }
    }
  })

  return (
    <div className="flex flex-col gap-[1rem] m-[1rem]">
      <div className="flex justify-between">
        <input
          value={globalFilter}
          onChange={e => setGlobalFilter(e.target.value)}
          placeholder='Search All columns'
          className='flex-1 focus:outline-none'
        />
        <div className="flex gap-[1rem]">
          <button onClick={() => table.options.meta.addRow()} className='w-[7rem] bg-blue-500 rounded text-white px-4 py-2 ml-2'>Add Row</button>
          <button onClick={() => setShowNewColumnForm(true)} className='bg-yellow-500 rounded text-white px-4 py-2'>Add Column</button>
          <button onClick={() => onSave()} className='bg-green-500 rounded text-white px-4 py-2'>Update</button>
        </div>
      </div>
      {showNewColumnForm && (
        <div className="flex flex-col mt-[2rem]">
          <h2 className="text-xl font-bold mb-2">Add New Column</h2>
          <label htmlFor="newColumn" className="text-gray-700">Column Name:</label>
          <input
            type='text'
            id='newColumn'
            value={newColumn}
            onChange={e => setNewColumn(e.target.value)}
            className="border rounded px-3 py-2 mt-1 mb-2 w-[24rem] required"
          />
          <div className="flex gap-[1rem]">
            <button onClick={() => { table.options.meta.addColumn(); setShowNewColumnForm(false); }} className="bg-sky-400 text-white px-4 py-2 rounded">
              Add column
            </button>
            <button onClick={() => setShowNewColumnForm(false)} className="bg-red-400 text-white px-4 py-2 rounded">
              Cancel
            </button>
          </div>
        </div>
      )}
      <table className="w-full text-left text-sm text-gray-600">
        <thead className="text-xs text-gray-700 uppercase bg-gray-50">
          {table.getHeaderGroups().map(hg => (
            <tr key={hg.id}>
              {hg.headers.map(h => (
                <th key={h.id} className='relative border px-6 py-3' style={{ width: `${h.getSize()}px` }}>
                  <div className="flex">
                    <span>{h.column.columnDef.header}</span>
                    <button onClick={h.column.getToggleSortingHandler()}>
                      <span className='text-yellow-500 ml-[0.5rem]'>
                        {{
                          asc: " 🔼",
                          desc: " 🔽",
                        }[h.column.getIsSorted()] ||
                          <FontAwesomeIcon icon={faUpDown} />
                        }
                      </span>
                    </button>
                    <button onClick={() => { setColumnToDelete(h.column.columnDef.accessorKey); setShowDeleteColumnDialog(true) }} className='absolute right-[0.5rem] top-[1.7rem] text-red-500'>
                      <FontAwesomeIcon icon={faTrash} />
                    </button>
                    <div
                      onMouseDown={h.getResizeHandler()}
                      onTouchStart={h.getResizeHandler()}
                      className="absolute top-0 right-0 h-full w-[5px] hover:bg-gray-300 cursor-col-resize"
                    >
                    </div>
                  </div>
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map(r => (
            <tr key={r.id}>
              {r.getVisibleCells().map(c => (
                <td key={c.id} className='border p-[0.25rem]'>
                  {flexRender(c.column.columnDef.cell, c.getContext())}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
      <div className="flex gap-[0.5rem]">
        <span className='text-sm'>
          Page {table.getState().pagination.pageIndex + 1} of {table.getPageCount()}
        </span>
        <button
          disabled={!table.getCanPreviousPage()}
          onClick={() => table.previousPage()}
          className='px-[0.5rem] py-[0.25rem] border border-gray-300 rounded'
        >
          &lt;
        </button>
        <button
          disabled={!table.getCanNextPage()}
          onClick={() => table.nextPage()}
          className='px-[0.5rem] py-[0.25rem] border border-gray-300 rounded'
        >
          &gt;
        </button>
        <select
          value={table.getState().pagination.pageSize}
          onChange={e => {
            table.setPageSize(Number(e.target.value))
          }}
          className='px-[0.5rem] py-[0.25rem] border border-gray-300 rounded'
        >
          {[10, 20, 30, 40, 50, 100, 200, 500].map(pageSize => (
            <option key={pageSize} value={pageSize}>
              {pageSize}
            </option>
          ))}
        </select>
      </div>
      <div className="self-center flex gap-[1rem]">
        <Link to='/spreadsheet' className="self-center bg-rose-500 rounded text-white px-4 py-2">Go Back</Link>
        <button onClick={() => download()} className="self-center bg-rose-500 rounded text-white px-4 py-2">Download</button>
      </div>
      {showDeleteColumnDialog && (
        <div onClick={() => setShowDeleteColumnDialog(false)} className="fixed top-0 left-0 w-full h-full bg-black/50 flex items-center justify-center">
          <div onClick={e => e.stopPropagation()} className="bg-white p-[2rem] rounded">
            <p className="text-xl font-bold mb-4">Are you sure you want to delete column <span className="uppercase">{columnToDelete}</span>?</p>
            <div className="flex justify-end">
              <button onClick={() => setShowDeleteColumnDialog(false)} className="bg-gray-300 text-gray-800 px-4 py-2 rounded mr-2">
                Cancel
              </button>
              <button onClick={() => { table.options.meta.removeColumn(); setShowDeleteColumnDialog(false) }} className="bg-red-500 text-white px-4 py-2 rounded">
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>

  )
}

function EditableCell({ getValue, row, column, table }) {
  const initalValue = getValue();
  const [value, setValue] = useState(initalValue);

  useEffect(() => {
    setValue(initalValue);
  }, [initalValue])

  function onChange(value) {
    setValue(value);
    table.options.meta.updateData(row.index, column.id, value);
  }

  return (
    <input
      type='text'
      value={value}
      onChange={e => onChange(e.target.value)}
      className='w-full px-3 py-2'
    />
  )
}

export default Table