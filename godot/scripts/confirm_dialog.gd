class_name ConfirmDialog extends Control

signal confirmed(is_confirmed: bool)

@onready var header_label: Label = %HeaderLabel
@onready var message_label: Label = %MessageLabel

@onready var cancel_button: Button = %CancelButton
@onready var action_button: Button = %ActionButton

var is_open: bool = false

func _ready() -> void:
	set_process_unhandled_key_input(false)
	action_button.pressed.connect(_on_action_pressed)
	cancel_button.pressed.connect(_on_cancel_pressed)
	hide()
	
func _unhandled_key_input(event: InputEvent) -> void:
	if event.is_action_pressed("ui_cancel"):
		cancel()
		
func customize(header: String, message: String, actionText: String, cancelText: String) -> ConfirmDialog:
	header_label.text = header
	message_label.text = message
	action_button.text = actionText
	cancel_button.text = cancelText
	return self
		
func confirm() -> void:
	_close_modal(true)
	
func cancel() -> void:
	_close_modal(false)
		
func _close_modal(is_confirmed: bool) -> void:
	set_process_unhandled_input(false)
	confirmed.emit(is_confirmed);
	set_deferred('is_open', false)
	hide()
			
		
func _on_action_pressed() -> void:
	confirm()
	
func _on_cancel_pressed() -> void:
	cancel()
