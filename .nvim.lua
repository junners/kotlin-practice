local project_root = vim.fn.fnamemodify(debug.getinfo(1, "S").source:sub(2), ":p:h")

local kotlin_cmd = { "mise", "exec", "--", "kotlin-language-server" }

local function kotlin_root()
  return project_root
end

if vim.lsp and vim.lsp.config then
  vim.lsp.config("kotlin_language_server", {
    cmd = kotlin_cmd,
    root_dir = kotlin_root,
    root_markers = { "settings.gradle.kts", "settings.gradle", "build.gradle.kts", "build.gradle", ".git" },
  })
  vim.lsp.enable("kotlin_language_server")
end

local lspconfig_ok, lspconfig = pcall(require, "lspconfig")
if lspconfig_ok and lspconfig.kotlin_language_server then
  lspconfig.kotlin_language_server.setup({
    cmd = kotlin_cmd,
    root_dir = kotlin_root,
  })
end
