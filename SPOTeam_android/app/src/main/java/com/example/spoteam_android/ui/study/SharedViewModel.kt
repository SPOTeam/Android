import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _selectedLocation = MutableLiveData<String>()
    val selectedLocation: LiveData<String> get() = _selectedLocation

    fun selectLocation(location: String) {
        _selectedLocation.value = location
    }
}
