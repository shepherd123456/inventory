import { Link } from "react-router-dom"

function Home() {
  return (
    <div className="h-screen flex flex-col justify-center items-center gap-[1rem]">
      <Link to='/spreadsheet' className="bg-sky-500 rounded text-white px-4 py-2">
        Log in
      </Link>
    </div>
  )
}

export default Home