import Navbar from './components/Navbar'
import AccommodationList from './components/AccommodationList'
import './App.css'

function App() {
  return (
      <div>
        <Navbar />

        <main>
          <h2>Találd meg a számodra megfelelő szállást!</h2>
          <AccommodationList />
        </main>
      </div>
  )
}

export default App