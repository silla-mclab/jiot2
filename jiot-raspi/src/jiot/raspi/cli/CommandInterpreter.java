package jiot.raspi.cli;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import jiot.raspi.thing.ControlPoint;
import jiot.raspi.thing.ControlPointContainer;
import jiot.raspi.thing.OutputControlPoint;
import jiot.raspi.ext_dev.CommandExecutable;
import jiot.raspi.ext_dev.ExtendedInput;

public class CommandInterpreter {

    private static AtomicReference<CommandInterpreter> instance
            = new AtomicReference<CommandInterpreter>();

    public static CommandInterpreter getInstance() {
        if (instance.get() == null) {
            instance.set(new CommandInterpreter());
         }
        return instance.get();
    }

    public interface Command {
        public String execute(String[] command);
        public String getHelp();
    }

    private Map<String, Command> commands = new HashMap<String, Command>();

    private CommandInterpreter() {
        commands.put("list", new Command() {
            @Override
            public String execute(String[] command) {
                StringBuilder sb = new StringBuilder();
                Collection<ControlPoint> points
                        = ControlPointContainer.getInstance().getControlPoints();
                sb.append("ControlPointContainer has " + points.size() +"'s control points.")
                  .append(System.lineSeparator());
                for (ControlPoint point : points) {
                    sb.append(point.toString())
                            .append(System.lineSeparator());
                  }
 
                return sb.toString();
             }

            @Override
            public String getHelp() {
                return "list: display a list for control points";
             }
         });

        commands.put("set", new Command() {
            @Override
            public String execute(String[] command) {
                if (command.length != 3) {
                    return "Invalid set command\nUsage: set cp-id value";
                } else {
                    int pointId = Integer.parseInt(command[1]);
                    ControlPoint point
                            = ControlPointContainer.getInstance().getControlPoint(pointId);
                    if (point == null) {
                        return "Cannot find a point(" + pointId + ")";
                    } else if (point instanceof OutputControlPoint) {
                        int value = Integer.parseInt(command[2]);
                        OutputControlPoint writablePoint = (OutputControlPoint)point;
                        writablePoint.setPresentValue(value);
                        return null;
                    } else {
                        return "It is not a output point(" + pointId + ")";
                    }
                }
             }

            @Override
            public String getHelp() {
                return "set: set the present value of point. format"
                        + "-> set [point id] [value]";
             }
         });

        commands.put("get", new Command() {
            @Override
            public String execute(String[] command) {
                if (command.length != 2) {
                    return "Invalid get command";
                } else {
                    int pointId = Integer.parseInt(command[1]);
                    ControlPoint point
                            = ControlPointContainer.getInstance().getControlPoint(pointId);
                    if (point == null) {
                        return "Cannot find a point(" + pointId + ")";
                    } else {
                        return String.valueOf((point.getType()==ControlPoint.Type.AIE) ? 
                                ((ExtendedInput)point).getValue() : point.getPresentValue());
                    }
                }
             }

            @Override
            public String getHelp() {
                return "get: get the present value of point. format"
                        + "-> get [point id]";
             }
         });
        
        commands.put("rename", new Command() {
            @Override
            public String execute(String[] command) {
                if (command.length != 3) {
                    return "Invalid rename command";
                } else {
                    int pointId = Integer.parseInt(command[1]);
                    ControlPoint point
                            = ControlPointContainer.getInstance().getControlPoint(pointId);
                    if (point == null) {
                        return "Cannot find a point(" + pointId + ")";
                    } else {
                        point.setName(command[2]);
                        return null;
                    }
                }
             }

            @Override
            public String getHelp() {
                return "rename: change the name of point. format"
                        + "-> rename [point id] [new name]";
             }
         });
        
        commands.put("exec", new Command() {
            @Override
            public String execute(String[] command) {
                if (command.length < 3) {
                    return "Invalid exec command";
                } else {
                    int pointId = Integer.parseInt(command[1]);
                    ControlPoint point
                            = ControlPointContainer.getInstance().getControlPoint(pointId);
                    if (point == null) {
                        return "Cannot find a point(" + pointId + ")";
                    } else if (point instanceof CommandExecutable) {
                        String[] subCommand = Arrays.copyOfRange(command, 2, command.length);
                        return ((CommandExecutable)point).executeCommmad(subCommand);
                    } else {
                        return "It is not a command-executable point(" + pointId + ")";
                    }
                }
             }
            
            @Override
            public String getHelp() {
                return "exec: execute extended command for control point. format"
                        + "-> exec [point id] [command] [value] ...";
             }
         });        
     }

    public String execute(String[] command) throws IOException {
        if (command.length == 0) {
            return null;
        }
        if (command[0].equals("help")) {
            return help();
        }

        Command cmd = commands.get(command[0]);
        if (cmd == null) {
            return "Invalid command: " + command[0];
        }

        return cmd.execute(command);
    }

    private String help() {
        StringBuilder sb = new StringBuilder();
        sb.append("Thing's commands")
                .append(System.lineSeparator());
        for (Command command : commands.values()) {
            sb.append(command.getHelp())
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }

    public void register(String name, Command cmd) {
        commands.put(name, cmd);
    }

    public void unregister(String name) {
        commands.remove(name);
    }
}