import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useDropzone } from "react-dropzone";
import { useCtx } from '../../../hooks/useCtx';

function Input() {
  const { ctx } = useCtx();

  const [isLoading, setIsLoading] = useState(false);
  const [spid, setSpid] = useState('');
  const [workNames, setWorkNames] = useState([]);
  const [filename, setFilename] = useState('');
  const [errMsg, setErrMsg] = useState('');

  const { getRootProps, getInputProps } = useDropzone({
    accept: '.xlsx, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    onDrop: acceptedFiles => {
      upload(acceptedFiles[0]);
    }
  });

  useEffect(() => {
    setErrMsg('');
  }, [isLoading, filename])

  async function load() {
    try {
      const res = await ctx.axios.get(`/spreadsheet/${filename}`);
      setSpid(res.data.spid);
      setWorkNames(res.data.workNames);
    } catch (err) {
      console.log(err);
      setWorkNames([]);
      if (!err.response) {
        setErrMsg('No server response');
      } else if (err.response.status === 404) {
        setErrMsg(`Spreadsheet filename "${filename}" does not exists`);
      } else if (err.response.status === 400) {
        setErrMsg('Similar file exists, you have to be more specific. Hint: the filename contains upload date and time in format "yyyy-MM-dd_hh-mm-ss" (year-month-day_hours-minutes-seconds). For example "2024-05-15_19-42-32". You can even enter part of it (for example 2 digit hours number or day number), as long as the file will be differentiated from similar.')
      }
    }
  }

  async function upload(file) {
    setIsLoading(true);
    const formData = new FormData();
    formData.append('file', file);
    const res = await ctx.axios.post(`/spreadsheet`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    setSpid(res.data.spid);
    setWorkNames(res.data.workNames);
    setIsLoading(false);
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

  return (
    <div className="flex flex-col m-[1rem] gap-[1rem]">
      {isLoading && <div className="fixed top-0 left-0 w-full h-full flex items-center justify-center bg-black bg-opacity-50 z-50">
        <div className="animate-spin rounded-full h-24 w-24 border-t-8 border-gray-200"></div>
      </div>}
      <h1 className="self-center text-xl">Construction Works</h1>
      <span className={errMsg ? 'rounded bg-red-200 text-red-700 font-medium mb-2 p-2' : 'hidden'}>{errMsg}</span>
      <div className="flex justify-between gap-[1rem]">
        <div className="flex gap-[5rem]">
          <div {...getRootProps()} className="border border-dashed border-gray-300 p-[1rem] cursor-pointer">
            <input {...getInputProps()} />
            <span>Drag and drop new spreadsheet</span>
          </div>
        </div>
        <div className="flex gap-[1rem] items-center">
          <input
            type='text'
            id='spFilename'
            value={filename}
            onChange={e => setFilename(e.target.value)}
            placeholder="Spreadsheet filename"
            className="border rounded px-3 py-2 required"
          />
          <div className="flex gap-[1rem]">
            <button onClick={() => load()} className="bg-sky-400 text-white px-4 py-2 rounded">load</button>
          </div>
        </div>
      </div>
      <hr className="mt-[1rem]" />
      {workNames && (
        <div className="grid grid-cols-7 gap-[1rem] my-[1rem]">
          {workNames.map((w, i) => (
            <Link to="/spreadsheet/table" state={{ spid, workName: w }} key={i} className={`${i % 2 === 0 ? 'bg-gray-300' : 'bg-gray-400'} rounded text-center px-4 py-2`}>{w}</Link>
          ))}
        </div>
      )}
      <button onClick={() => download()} className={`${workNames.length === 0 ? 'hidden' : 'inline'} self-center w-[15rem] bg-green-400 text-white px-4 py-2 rounded mt-[1rem]`}>Download spreadsheet</button>
    </div>
  )
}

export default Input