extends Control

@onready var confirm_dialog: ConfirmDialog = %ConfirmDialog
@onready var scan_btn: Button = %ScanDevices
@onready var result_box: VBoxContainer = %ResultContainer
@onready var device_list: VBoxContainer = %DeviceList

var button_pck = preload("res://base_components/button.tscn")

func _ready() -> void:
	result_box.hide()
	
	PianoManager.confirm_bluetooth_access.connect(_open_confirm_bluetooth_access)
	PianoManager.confirm_location_access.connect(_open_confirm_location_access)
	PianoManager.scan_started.connect(_on_scan_started)
	PianoManager.scan_stopped.connect(_on_scan_stopped)
	PianoManager.new_device_found.connect(_update_result_list)
	PianoManager.device_connected.connect(PianoManager.start_game)
	
func _update_result_list() -> void:
	var devices: PackedStringArray = PianoManager.get_devices()
	
	# clean up old devices
	for child in device_list.get_children():
		device_list.remove_child(child)
	
	for device in devices:
		var btn: Button  = button_pck.instantiate()
		btn.label = device
		btn.pressed.connect(_on_connect_device.bind(device))
		device_list.add_child(btn)

func _on_connect_device(device_name: String) -> void:  
	PianoManager.connect_to_device(device_name) 

func _on_scan_started() -> void:
	result_box.show()
	scan_btn.hide()
	
func _on_scan_stopped() -> void:
	result_box.hide()
	scan_btn.show()

func _on_back_pressed() -> void:
	PianoManager.to_main_menu()

func _on_scan_devices_pressed() -> void:
	PianoManager.scan_for_devices()

func _open_confirm_bluetooth_access() -> void:
	confirm_dialog.customize("Allow bluetooth access", "To find the piano the app needs to access bluetooth.", "Allow", "Cancel")
	confirm_dialog.show()
	confirm_dialog.confirmed.connect(_confirm_bluetooth_access_callback)
	
func _confirm_bluetooth_access_callback(confirmed: bool) -> void:
	confirm_dialog.confirmed.disconnect(_confirm_bluetooth_access_callback)
	
	if confirmed:
		PianoManager.enable_bluetooth();

func _open_confirm_location_access() -> void:
	confirm_dialog.customize("Allow location access", "To find the piano the app needs to access to the location of the phone.", "Allow", "Cancel")
	confirm_dialog.show()
	confirm_dialog.confirmed.connect(_confirm_location_access_callback)
	
func _confirm_location_access_callback(confirmed: bool) -> void:
	confirm_dialog.confirmed.disconnect(_confirm_location_access_callback)
	
	if confirmed:
		PianoManager.enable_location()

func _on_cancel_pressed() -> void:
	PianoManager.stop_scan()
