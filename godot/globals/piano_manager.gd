extends Node

var save_file_path = 'user:://save-game-0.0.0.save'

var connector_plugin_name = "PianoConnectorPlugin"
var connector_plugin

const BLUETOOTH_SCAN_STARTED = "started"
const BLUETOOTH_SCAN_LOCATION_DISABLED = "locationDisabled"
const BLUETOOTH_SCAN_BLUETOOTH_DISABLED = "bluetoothDisabled"
const BLUETOOTH_SCAN_NEW_DEVICES = "newDevices"
const BLUETOOTH_SCAN_STOPPED = "stopped"
const BLUETOOTH_SCAN_DEVICE_CONNECTED = "deviceConnected"

const DEVICE_LIST_SEPARATOR = "<,>"

signal confirm_location_access()
signal confirm_bluetooth_access()
signal scan_started()
signal scan_stopped()
signal new_device_found()
signal device_connected()
signal note_pressed(note: int)

var is_loaded = false
var is_device_connected = false

func _ready() -> void:
	if Engine.has_singleton(connector_plugin_name):
		connector_plugin = Engine.get_singleton(connector_plugin_name)
		connector_plugin.bluetoothHandler.connect(_handle_bluetooth_event)
		connector_plugin.noteEventHandler.connect(_handle_note_event)
		is_loaded = true
	else:
		is_loaded = false
		printerr("Couldn't find plugin " + connector_plugin_name)


func _load_save():
	var save_file = FileAccess.open(save_file_path, FileAccess.READ)
	# TODO add save file logic


func switch_scene(path: String) -> void:
	get_tree().call_deferred("change_scene_to_file", path)

func _handle_note_event(event: String) -> void:
	print("Event: " + event);
	
	var dataParts = event.split(",")
	var eventType = dataParts.get(0)
	
	if eventType == "NoteOn":
		note_pressed.emit(int(dataParts.get(1)))
	

func _handle_bluetooth_event(event: String) -> void:
	print("Event: " + event);
		
	if(event == BLUETOOTH_SCAN_LOCATION_DISABLED):
		confirm_location_access.emit()
		return
	
	if(event == BLUETOOTH_SCAN_BLUETOOTH_DISABLED):
		confirm_bluetooth_access.emit()
		return
		
	if(event == BLUETOOTH_SCAN_STARTED):
		scan_started.emit()
		return
		
	if(event == BLUETOOTH_SCAN_NEW_DEVICES):
		new_device_found.emit()
		return
		
	if(event == BLUETOOTH_SCAN_STOPPED):
		scan_stopped.emit();
		return
		
	if(event == BLUETOOTH_SCAN_DEVICE_CONNECTED):
		is_device_connected = true
		device_connected.emit()
		return

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
	
func enable_bluetooth() -> void:
	connector_plugin.enableBluetooth()
	
func connect_to_device(device_name: String) -> void:
	connector_plugin.connectToScanResult(device_name)

func start_game() -> void:
	if !is_device_connected:
		switch_scene("res://scenes/piano_setup/setup_menu.tscn")
		return
	
	switch_scene("res://scenes/type_game/type_game.tscn")

func to_main_menu() -> void:
	switch_scene("res://scenes/main_menu/main_menu.tscn")
	
	
