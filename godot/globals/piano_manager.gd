extends Node

var save_file_path = 'user:://save-game-0.0.0.save'

var connector_plugin_name = "PianoConnectorPlugin"
var connector_plugin

const BLUETOOTH_SCAN_STARTED = "started"
const BLUETOOTH_SCAN_LOCATION_DISABLED = "locationDisabled"
const BLUETOOTH_SCAN_BLUETOOTH_DISABLED = "bluetoothDisabled"
const BLUETOOTH_SCAN_NEW_DEVICES = "newDevices"
const BLUETOOTH_SCAN_STOPPED = "stopped"

const DEVICE_LIST_SEPARATOR = "<,>"

signal confirm_location_access()
signal confirm_bluetooth_access()
signal scan_started()
signal scan_stopped()
signal new_device_found()

var is_loaded = false
var is_connected = false

func _ready() -> void:
	if Engine.has_singleton(connector_plugin_name):
		connector_plugin = Engine.get_singleton(connector_plugin_name)
		connector_plugin.bluetoothHandler.connect(_handle_bluetooth_event)
		is_loaded = true
	else:
		is_loaded = false
		printerr("Couldn't find plugin " + connector_plugin_name)


func _load_save():
	var save_file = FileAccess.open(save_file_path, FileAccess.READ)
	# TODO add save file logic


func switch_scene(path: String) -> void:
	get_tree().call_deferred("change_scene_to_file", path)


func _handle_bluetooth_event(event: String) -> void:
	print("Event: " + event);
		
	if(event == BLUETOOTH_SCAN_LOCATION_DISABLED):
		confirm_location_access.emit()
		pass
	
	if(event == BLUETOOTH_SCAN_BLUETOOTH_DISABLED):
		confirm_bluetooth_access.emit()
		pass
		
	if(event == BLUETOOTH_SCAN_STARTED):
		scan_started.emit()
		pass
		
	if(event == BLUETOOTH_SCAN_NEW_DEVICES):
		new_device_found.emit()
		pass
		
	if(event == BLUETOOTH_SCAN_STOPPED):
		scan_stopped.emit();
		pass
		

	print("devices: " + connector_plugin.getScanResults())
	
func get_devices() -> PackedStringArray:
	var results: String = connector_plugin.getScanResults()
	return results.split(DEVICE_LIST_SEPARATOR)

func scan_for_devices() -> void:
	connector_plugin.scanBLEDevices()
	
func stop_scan() -> void:
	connector_plugin.stopScan()
	
func enable_location() -> void:
	connector_plugin.enableLocation()
	
func enable_bluetooth() ->void:
	connector_plugin.enableBluetooth()

func start_game() -> void:
	if !is_connected:
		switch_scene("res://piano_setup/setup_menu.tscn")


func to_main_menu() -> void:
	switch_scene("res://main_menu/main_menu.tscn")
	
	
