import copy

class TSLWriterFormatter:
    
  def get_indent_string(self):
    return self.__indent_str

  def get_indent(self):
    return self.__indent

  def get_newline(self):
    return self.__newline

  def set_indent(self,indent):
    if indent < 0:
      indent = 0
    self.__indent = indent
    self.__indent_str = ""
    for i in range(0,indent):
      self.__indent_str = self.__indent_str + " "
    return self

  def set_newline(self,newline):
    self.__newline = bool(newline)
    return self
    
  def __init__(self,indent,newline):
    self.__newline = bool(newline)
    self.set_indent(indent)

def _validate_object_name(name):
  if not _is_valid_object_name(name):
    raise ValueError("Invalid TSL Object name: '" + name + "'.")

def _is_valid_object_name(name):
  for c in name:
    if not (c.isalpha() or c.isdigit() or c == '_' or c == '-'):
      return False
  return True
  
def _encode_value(value):
  return value.replace('"',"\\\"").replace('\\',"\\\\")

class TSLWriter:
  def __write_separator(self,put_comma):
    if self.__root_element:
      return

    if put_comma and not self.__first_element:
      self.__osw.write(',')

    if self.formatter.get_newline():
      self.__osw.write("\n")
      for i in range(0,self.__level):
        self.__osw.write(self.formatter.get_indent_string())
    else:
      self.__osw.write(' ')
  

  
  def __init__(self,filename):
    self.__osw = open(filename,"w")
    self.__level = 0
    self.__first_element = True
    self.__root_element = True
    self.__closed = False
    self.formatter = TSLWriterFormatter(2,True)
    self.__formatter_stack = []
  
  def push_formatter(self):
    self.__formatter_stack.append(self.formatter)
    self.formatter = copy.copy(self.formatter)
  
  def pop_formatter(self):
    if len(self.__formatter_stack) > 0:
      self.formatter = self.__formatter_stack.pop()
  
  def put_value(self,name,value):
    name = str(name)
    value = str(value)
    _validate_object_name(name)
    if self.__closed:
      raise ValueError("Root TSL Object already closed.")
    if self.__root_element:
      self.__closed = True
    self.__write_separator(True)

    self.__osw.write(name)
    self.__osw.write(" \"")
    self.__osw.write(_encode_value(value))
    self.__osw.write('"')
    
    self.__first_element = False
  
  def start_collection(self,name):
    name = str(name)
    _validate_object_name(name)

    if self.__closed:
      raise ValueError("Root TSL Object already closed.")
    
    self.__write_separator(True)

    self.__osw.write(name)
    self.__osw.write(" [")
    self.__first_element = True
    self.__root_element = False
    self.__level += 1
  
  def end_collection(self):
    if self.__level == 0 or self.__closed:
      raise ValueError("Root TSL Object already closed.")      
    if self.__first_element:
      raise ValueError("Cannot close an empty TSL Collection.")
    self.__level -= 1
    self.__write_separator(False)

    self.__osw.write("]")
    if self.__level == 0:
      self.__osw.close()
      self.__closed = True
    root_element = False
    first_element = False

