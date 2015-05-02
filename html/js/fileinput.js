$(document).on('change', '.btn-file :file', function() {
        var input = $(this);
        $("#fake_file").val(input.val());
});