extends Control

var save_file_path = 'user:://save-game-0.0.0.save'
var connector_plugin_name = "PianoConnectorPlugin"
var connector_plugin

const BLUETOOTH_SCAN_STARTED = "started"
const BLUETOOTH_SCAN_LOCATION_DISABLED = "locationDisabled"
const BLUETOOTH_SCAN_NEW_DEVICES = "newDevices"
const BLUETOOTH_SCAN_STOPPED = "stopped"
	
func _load_game():
	var save_file = FileAccess.open(save_file_path, FileAccess.READ)
	

func _ready():
	if Engine.has_singleton(connector_plugin_name):
		connector_plugin = Engine.get_singleton(connector_plugin_name)
		connector_plugin.bluetoothHandler.connect(handleBluetoothEvent)
		
	else:
		printerr("Couldn't find plugin " + connector_plugin_name)
		$VBoxContainer/Play.disabled = true
		$VBoxContainer/ErrorLabel.text = "Error: Could not load piano plugin :/"

func _on_play_pressed() -> void:
	print(connector_plugin.scanBLEDevices());
	

func handleBluetoothEvent(event) -> void:
	if(event == BLUETOOTH_SCAN_LOCATION_DISABLED):
		
	print("Event" + event);
	print("devices: " + connector_plugin.getScanResults())
